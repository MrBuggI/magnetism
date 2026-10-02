# Magnetism - магнитный блок (NeoForge 1.21.1)

[![Build](https://github.com/MrBuggI/magnetism/actions/workflows/build.yml/badge.svg)](https://github.com/MrBuggI/magnetism/actions/workflows/build.yml)
[![Release](https://img.shields.io/github/v/release/MrBuggI/magnetism)](https://github.com/MrBuggI/magnetism/releases/latest)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-62B47A)
![Loader](https://img.shields.io/badge/loader-NeoForge-F08A2A)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

Магнитный блок: притягивает металл по тегу предметов, падает без опоры не теряя
свойств, вращается и сохраняет поворот при падении, полюса притягиваются и
отталкиваются в полёте. Проект сделан по техническому заданию с упором на
совместимость и производительность в больших сборках.

**Миксинов: 0. Своих сетевых пакетов: 0. `Overwrite`: нет (нечего перезаписывать).**

> **English:** a NeoForge 1.21.1 magnet block. It attracts items by an item tag, falls like sand while keeping its rotation, and magnets interact through a real dipole model (like poles repel, opposite poles attract) while falling. Zero mixins, zero custom network packets, cached entity lookups with backoff. The architecture and performance notes below are in Russian.

## Установка

1. Установите [NeoForge](https://neoforged.net/) 21.1.x для Minecraft 1.21.1.
2. Скачайте `.jar` из раздела [Releases](https://github.com/MrBuggI/magnetism/releases/latest) и положите в папку `mods/`.
3. Блок `magnet_block` находится во вкладке креатива.

- mod id: `magnetism`
- пакет: `io.github.mrbuggi.magnetism`
- блок: `magnet_block`
- NeoForge: `21.1.235`, Minecraft `1.21.1`, Java 21
- плагин сборки: `net.neoforged.gradle.userdev 7.1.38` (из MDK 1.21.1)

## Архитектура

| Файл | Роль |
|---|---|
| `Magnetism` | точка входа `@Mod`, регистрация DeferredRegister и вкладки |
| `block/MagnetBlock` | `FallingBlock` + `EntityBlock`, свойство `FACING` (6 сторон) |
| `block/MagnetBlockEntity` | только тикер, персистентных данных нет |
| `magnet/MagnetForces` | физика: диполь-диполь и притяжение предметов |
| `magnet/ItemMagnetCache` | двухуровневый кэш ID предметов вокруг одного магнита |
| `magnet/FallingMagnetManager` | реестр летящих магнитов + попарные силы |
| `magnet/MagnetSettings` | все числа физики в одном месте: настраиваемые значения и константы модели |
| `config/MagnetConfig` | серверный конфиг; при загрузке раскладывает значения в `MagnetSettings` |
| `event/MagnetEventHandler` | 4 слушателя игровой шины NeoForge |
| `registry/*` | `DeferredRegister` блока, предмета, block entity + `TagKey` |
| `datagen/*` | генерация блокстейта, моделей, лута, рецепта, тегов и переводов |

### Почему без миксина

Ванильный `FallingBlockEntity` хранит **целиком** `BlockState` (приватное поле
`blockState`):

- пишется в NBT - `addAdditionalSaveData` → `NbtUtils.writeBlockState(this.blockState)`;
- уходит в spawn-пакет - `getAddEntityPacket` шлёт `Block.getId(getBlockState())`,
  а `recreateFromPacket` восстанавливает `Block.stateById(...)`;
- ставится ровно тем же при приземлении - `this.level().setBlock(pos, this.blockState, 3)`.

Значит `FACING` переживает падение сам по себе (проверено по декомпилированным
исходникам `net.minecraft.world.entity.item.FallingBlockEntity` для NeoForge
21.1.235) - трогать сущность не нужно. Остаётся добавить поведение в полёте, и для
этого хватает событий NeoForge: `EntityJoinLevelEvent` / `EntityLeaveLevelEvent`
(пакет `...event.entity`), `LevelTickEvent.Post` (`...event.tick`),
`LevelEvent.Unload` (`...event.level`). Все имена и пакеты сверены с исходниками
NeoForge в зависимостях.

Итог: ни одного миксина. Это критично для сборки на сотни модов - нет риска
конфликта байткод-патчей и нет `Overwrite`, который ломает совместимость.

### Оптимизация (с цифрами)

1. **Летящие магниты не ищутся поиском сущностей вообще.** Они сами
   регистрируются в `FallingMagnetManager` на `EntityJoinLevelEvent` и снимаются
   на `EntityLeaveLevelEvent`. Реестр - `Map<ResourceKey<Level>, List<Entry>>`.
   Попарный проход O(n²) идёт по списку из типично 0-3 элементов: при n=3 это
   3 пары против одного AABB-запроса, который перебирает все сущности в кубе
   16×16×16. `getEntitiesOfClass` для магнитов не вызывается **ни разу**.
   Доступ только с серверного потока (события и тикер сервера), поэтому
   синхронизация не нужна.
2. **Предметы - двухуровневый кэш.** Полный AABB-поиск раз в `rescanInterval` (по умолчанию 10)
   тиков, результат кэшируется как `int[]` ID. В остальные 9 тиков из 10 -
   `ServerLevel#getEntity(int)`, это O(1) по хэш-таблице сущностей. Т.е. дорогой
   поиск делается в ~10 раз реже.
3. **Backoff при пустоте.** Если поиск ничего не нашёл, следующий - через
   `idleRescanInterval` (по умолчанию 40) тиков. Магнит в пустом чанке стоит один декремент
   счётчика в тик (`cooldown--`) - фактически бесплатен.
4. **Потолок `maxTrackedItems` (по умолчанию 32).** Куча из 500 сброшенных предметов не
   раздует кэш и не превратит попарную обработку в квадрат по 500.
5. **Сеть: своих пакетов 0.** Чтобы движение доехало до клиента, хватает
   ванильного поля `Entity#hasImpulse` - трекер сущности сам отправит дельту в
   этот тик. Флаг выставляется не каждый тик, а раз в 4 (`SYNC_MASK = 3`): клиент
   между корректировками сам досчитывает гравитацию и трение. Получается ~5
   корректирующих пакетов в секунду на предмет вместо 20.

### Физика полюсов (честный диполь, без ветвлений)

Каждый магнит - диполь: два заряда `+1` (северный полюс) и `-1` (южный) на
расстоянии `POLE_OFFSET = 0.5` блока вдоль `FACING`. Сила между двумя магнитами -
сумма по 4 парам полюсов:

```
F = q₁·q₂·k / r²   (в коде: scalar = qa*qb*magnetForce / (r² · r), вектор d·scalar)
```

с софтенингом `r² ≥ MIN_DIST_SQR = 0.25`, чтобы не делить на ноль.

- одноимённые полюса → `q₁q₂ > 0` → вектор направлен **от** чужого полюса →
  отталкивание;
- разноимённые → `q₁q₂ < 0` → притяжение.

Никакого `if (samePole)` в коде нет - знак заряда всё делает сам. Итоговый импульс
обязательно клампится (`MAGNET_MAX_STEP`), иначе блоки улетают за карту.

Предметы (ферромагнетик, не диполь) тянутся к **ближнему** полюсу магнита - так
металл липнет к магниту с любой стороны, что физически корректнее, чем тянуть к
центру.

Установленный (не падающий) магнит тоже толкает пролетающие мимо магниты, но сам
не двигается: `MagnetBlockEntity.serverTick` вызывает
`FallingMagnetManager.pushFalling`, применяя силу только к летящей стороне.

## Настройка

- **Что притягивается** - тег предметов `data/magnetism/tags/item/magnetic.json`.
  Обычный ванильный тег, правится любым датапаком: добавьте/уберите предметы или
  ссылки на другие теги (`#c:ingots/iron` и т.п.). Хардкода списка в Java нет,
  код читает только `ModTags.MAGNETIC`.
- **Сила / радиус / интервалы** - серверный конфиг
  `<мир>/serverconfig/magnetism-server.toml`. Он хранится вместе с миром и
  приходит клиентам с сервера, поэтому у всех игроков значения одинаковые.

В версии 1.0.0 конфига не было намеренно: так требовало техническое задание, все
числа менялись пересборкой. В 1.1.0 то, что владельцу сервера нужно подбирать под
свою сборку, вынесено в конфиг. Числа, которые описывают саму модель, остались
константами в `magnet/MagnetSettings`.

Параметры конфига:

| Параметр | По умолчанию | Смысл |
|---|---|---|
| `items.radius` | 6.0 | радиус притяжения предметов, блоков |
| `items.pull` | 0.30 | сила притяжения предметов |
| `magnets.radius` | 8.0 | радиус взаимодействия магнит-магнит |
| `magnets.force` | 0.55 | сила диполя |
| `performance.rescanInterval` | 10 | период поиска предметов, тиков |
| `performance.idleRescanInterval` | 40 | период поиска, когда рядом пусто |
| `performance.maxTrackedItems` | 32 | потолок кэша ID |

Конфиг читается не в тике. `MagnetConfig` слушает загрузку и перезагрузку файла и
один раз раскладывает значения в поля `MagnetSettings`; тикеры магнитов читают
обычные поля, как раньше читали константы.

Константы модели:

| Константа | Значение | Смысл |
|---|---|---|
| `ITEM_MAX_STEP` | 0.35 | кламп импульса предмета |
| `MAGNET_MAX_STEP` | 0.22 | кламп импульса диполя |
| `POLE_OFFSET` | 0.5 | смещение полюсов вдоль FACING |
| `MIN_DIST_SQR` | 0.25 | софтенинг расстояния |
| `SYNC_MASK` | 3 | `hasImpulse` раз в 4 тика |

## Ресурсы и датаген

JSON-файлы руками не пишутся. Блокстейт, модели, таблица лута, рецепт, теги и
переводы описаны в `datagen/*` и генерируются в `src/generated/resources`:

```bash
./gradlew runData
```

Сгенерированные файлы лежат в репозитории, для обычной сборки запускать датаген не
нужно. При переводе на датаген нашлась ошибка версии 1.0.0: блок требует инструмент
для дропа, но не входил ни в один тег `mineable`, поэтому в выживании не выпадал
вообще. Теперь он в `minecraft:mineable/pickaxe`.

## Сборка

```bash
./gradlew build
```

Первый прогон тянет NeoForge и декомпилированный Minecraft с
`https://maven.neoforged.net/releases` (и плагины с Gradle Plugin Portal) -
**нужен доступ в сеть**. После кэширования зависимостей сборка идёт офлайн.

## Что проверено фактически

- **Компиляция / полная сборка** - `./gradlew build` проходит.
- **API по исходникам** NeoForge/Minecraft 21.1.235 (grep по декомпилированным
  сорцам из зависимостей Gradle): `FallingBlockEntity` (хранение и восстановление
  `BlockState`), `FallingBlock`, `DeferredRegister.Blocks#registerBlock`,
  `registerSimpleBlockItem`, `simpleCodec`, события входа/выхода сущностей и тика
  уровня, `BuildCreativeModeTabContentsEvent#accept(ItemLike)`,
  `ServerLevel#getEntity(int)`, `ItemStack#is(TagKey)`.
- **Повороты blockstate** сверены с ванильными `observer.json` и `piston.json`:
  `facing=down → x:90`, `facing=up → x:270`, `east/south/west → y:90/180/270`.

## Что осталось непроверенным (нужен запуск игры)

- **Баланс сил** - `ITEM_PULL`, `MAGNET_FORCE` подобраны «на глаз», в игре почти
  наверняка захочется подкрутить.
- **Визуальная ориентация полюсов** - модель совпадает по поворотам с ванилью, но
  какая грань (`lodestone_top` = «север» / `iron_block` = «юг») смотрит куда
  именно, стоит глянуть глазами; если `up`/`down` перепутаны - поменять местами
  `x:90` и `x:270` в blockstate.
- **Приземление в воду / лаву** - поведение отдаёт ванильному `FallingBlockEntity`,
  краевые случаи не проверялись игрой.

## Известные ограничения

- Магнит **не доворачивается** в полёте (нет момента сил, только смещение). Смена
  поворота на лету потребовала бы своего пакета или миксина - по ТЗ не требуется.
- Ванильный `FallingBlockEntity` через ~30 секунд (600 тиков) полёта превращается
  в предмет - вечная левитация над другим магнитом невозможна by design.
- Притягиваются только `ItemEntity`. Мобы в железной броне - расширение на
  несколько строк в `MagnetForces`.
