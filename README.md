# Barber Shop Simulation

## Running the program

To run the simulation, clone this repository and execute:

* On Mac or Linux:

  ```bash
  ./gradlew run
  ```
* On Windows:

  ```bash
  gradlew.bat run
  ```

You will need a JDK installed for Gradle to build and run the project. A simple way to install one is:

```bash
brew install openjdk
```

Alternatively, you can run the [packaged jar](https://github.com/mir-uddin/JobyShopSim/releases/tag/v1-simulation) directly:

```bash
java -jar barbershopsim.jar
```

This still requires a Java runtime on your machine. You can check if Java is available with:

```bash
java -version
```

*Please let me know if you run into any issues building or running the project.*


## Implementation Details

### Assumptions

* The shop clock ticks every 60 seconds. On each tick, the program checks the shop state and performs necessary actions. The tick interval can be made smaller and the program will still work. If the interval is made larger than 5 minutes, state inconsistencies may appear because customer arrivals occur every 5 minutes in the current setup.
* The simulation always starts at 09:00, and no customer arrives before this time.
* The first customer always arrives at 09:00 regardless of haircut duration or customer arrival frequency.
* The simulation ends only when both conditions are met:
  1. The shop is closed.
  2. All barbers have finished their final haircuts.
     At that point no customers can enter and the program safely ends.
* Although I have extensive experience with date-time APIs, I chose a simple conversion formula from seconds to HH:MM for this simulation since we do not care about timezones or real dates.


### Code Structure

#### Models

The simulation generates `Event` objects for all shop activity. There are two high-level categories: `ShopEvent` and `CustomEvent`.

* `CustomEvent` includes internal events such as `ClockTick` and `AttemptHaircut`.

  * `ClockTick` fires every 60 seconds to check for haircut completions, shift changes, and to attempt new haircuts.
* `ShopEvent` represents events defined in the project requirements (example: `ShopOpen`, `ShopClose`, `ShiftStart`, `ShiftEnd`, `CustomerExit`, etc).

  * `CustomerExit` uses the `ExitType` enum to describe how a customer leaves.

The `Barber` enum defines all barbers A (Alice) to H (Hector).


#### State Management

State is maintained in a `State` object, which is updated by both the event generator and the event listener (described in the next section).

The state includes:

* `Chairs` - up to 4 chairs, each with an assigned barber, customer, and haircut end time.
* `WaitingArea` - a min-heap of size 4.
  I chose a min-heap instead of a queue for two reasons:
  1. When full, new customers are turned away.
  2. Frustrated customers leave after waiting 20 minutes.
     A min-heap makes these operations more flexible and future-proof.
* `ShiftInfo` - tracks which barbers are working, not working, or whose shifts have ended and are wrapping up haircuts.
  * `working` and `wrappingUpWork` are sets for easy add/remove.
  * `notWorking` is a queue to ensure that transitioning the not working barbers to working happens in a sequential order.


#### Event Generator and Listener

The simulation runs based on scheduled events and state transitions.

* `EventSource` publishes events (mainly `ShopEvent`, but also `CustomEvent`).
* `EventListener` listens and reacts.

The implementation uses:

* `ExecutorService` for scheduling at a frequency and delays.
* Guava `EventBus` for pub-sub.

##### EventSource

* For this version, the source is `EventGenerator`.
* A future version can use an `InputFile` event source.
* The generator:
  * Schedules clock ticks every 60 seconds.
  * Applies a timescale factor so that 10,000 simulation seconds run in 1 real second (simulation completes in under 4 seconds).
  * Schedules barber shifts at 4 hour intervals.
  * Schedules customer arrivals every 5 minutes.
  * Updates state when shifts change (retiring barbers, starting new ones).

##### EventListener

There can be multiple listeners (all employed simultaneously):

* `EventOutputter` logs simulation text to the CLI. A future GUI listener could update UI instead.
* `EventProcessor` is critical for state management:
  * Receives all events.
  * Updates state.
  * Posts follow-up events when needed.
  * Detects when the simulation should end.

Key logic happens on each `ClockTick` and `AttemptHaircut`:

* `ClockTick`
  * Detects completed haircuts.
  * Retires barbers when appropriate.
  * Triggers `AttemptHaircut`.

* `AttemptHaircut`
  * Evicts frustrated customers.
  * Pulls the highest priority customer from the waiting area.
  * Starts a new haircut if a chair is available.


#### Main and BusManager

* `Main` starts the program, registers event listeners, and initializes the event source.
* `BusManager` wraps EventBus usage and ensures `EventProcessor` is always subscribed.


### Tools used

* Java
* IntelliJ IDEA
* Guava EventBus
* Gradle for dependency management
* ChatGPT for documentation assistance


## Challenges encountered

* State management was the most time-consuming part and added roughly additional 3 hours of effort to understand the interdependencies within the state.
* Clock synchronization required iteration. Ultimately a separate scheduled clock tick proved more reliable and decoupled from customer arrival frequency (which was my naive approach).
* EventBus nuances caused issues. Posting events from within handlers is async, while posting from outside the handlers is synchronous. Once understood, ordering issues were fixed.


## Steps to improve

* More separation of concerns for state manipulation between `EventGenerator` and `EventProcessor`. Ideally all state updates should be in `EventProcessor`.
* Customer arrival frequency is configurable currently, but can only be changed in code. For InputFiles it can take the values from there.

* Add an input file reader plus deterministic unit tests. Multiple scenario files would allow comprehensive test coverage.
  * Define a preferred format for the input file. A tentative CSV format:
    ```
    N(customer), arrival time(HH,MM), cut duration(minutes)
    1,09:00,22
    3,09:10,39
    2,10:01,30
    ```

  * Create a corresponding expected-output file for each input file to verify simulation correctness. Example output format:
    ```
    RWB cuts is open for business!
    Alice started shift
    Bob started shift
    Charlie started shift
    Dave started shift
    Customer-1 entered
    Alice started cutting Customer-1's hair
    ...
    ```
* Mobile UI that updates itself using an EventListener.
