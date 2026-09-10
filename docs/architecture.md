# Architecture — Ports & Adapters

GymPlannerService is a multi-module Spring Boot app arranged as a **hexagonal
(ports-and-adapters)** system. Every feature is a vertical slice that depends only on
the shared base modules (`:domain`, `:core-utils`) — **never on another feature**. When
a feature needs something another feature owns, it declares a **port** (an interface it
owns) and consumes that. The concrete **adapters** that satisfy those ports all live in
`:app`, the single composition root that depends on every feature and wires them
together at runtime via Spring component scanning.

## Module layering

`:app` depends on every feature module plus `:domain`; `:security` is not a direct
`:app` dependency — it is pulled in transitively through `:authentication`, which is
the only module that uses it.

```mermaid
flowchart TD
    app[":app<br/>composition root · adapters · main()"]

    subgraph features["Feature modules (no feature → feature edges)"]
        direction LR
        authentication
        booking
        availability
        trainers
        checkin
        fitnessclass["fitness-class"]
        pushnotifications["push-notifications"]
        userprofile["user-profile"]
        others["exercises · fault-reporting · gym-locations<br/>messages · facility-status"]
    end

    domain[":domain<br/>entities · test fixtures"]
    coreutils[":core-utils<br/>common · validation · utils"]
    security[":security"]

    app --> features
    app --> domain
    features --> domain
    domain -->|api| coreutils
    authentication --> security
```

## Ports & adapters flow

Each edge is one dependency inversion: a **consumer's port** (left) is implemented by an
**adapter in `:app`** (edge label) that delegates to a **provider's bean** (right).

```mermaid
flowchart LR
    subgraph consumers["Consumer features — own the port interfaces"]
        direction TB
        bPT["booking<br/>PersonalTrainerGateway"]
        bAV["booking<br/>AvailabilityGateway"]
        bUP["booking<br/>UserProfileGateway"]
        bUSR["booking<br/>UserGateway"]
        bNS["booking<br/>NotificationSender"]
        aPT["availability<br/>PersonalTrainerGateway"]
        cPT["checkin<br/>PersonalTrainerGateway"]
        fUSR["fitness-class<br/>UserGateway"]
        fNS["fitness-class<br/>NotificationSender"]
        pUSR["push-notifications<br/>UserGateway"]
        auREG["authentication<br/>UserProfileRegistrar"]
    end

    subgraph providers["Provider features — repositories / senders"]
        direction TB
        trRepo["trainers<br/>PersonalTrainerRepository"]
        avRepo["availability<br/>AvailabilityRepository"]
        upRepo["user-profile<br/>UserProfileRepository"]
        usrRepo["authentication<br/>UserRepository"]
        fcm["push-notifications<br/>FcmSender"]
    end

    bPT  -->|"BookingPersonalTrainerGateway"| trRepo
    aPT  -->|"AvailabilityPersonalTrainerGateway"| trRepo
    cPT  -->|"CheckInPersonalTrainerGateway"| trRepo
    bAV  -->|"AvailabilityGatewayImpl"| avRepo
    bUP  -->|"BookingUserProfileGateway"| upRepo
    auREG -->|"UserProfileRegistrarAdapter"| upRepo
    bUSR -->|"BookingUserGateway"| usrRepo
    fUSR -->|"ClassesUserGateway"| usrRepo
    pUSR -->|"FcmUserGateway"| usrRepo
    bNS  -->|"FcmNotificationSender"| fcm
    fNS  -->|"ClassesNotificationSender"| fcm

    classDef port fill:#e3f2fd,stroke:#1565c0,color:#0d47a1;
    classDef prov fill:#e8f5e9,stroke:#2e7d32,color:#1b5e20;
    class bPT,bAV,bUP,bUSR,bNS,aPT,cPT,fUSR,fNS,pUSR,auREG port;
    class trRepo,avRepo,upRepo,usrRepo,fcm prov;
```

> All 11 adapters (the edge labels) are `@Component`s under
> `app/.../gateway/`. Because every feature shares the `com.ianarbuckle.gymplannerservice`
> base package, Spring's component scan from `:app` discovers ports (in features) and
> adapters (in `:app`) and injects the adapter wherever a feature's service asks for its
> port.

## Why this shape

- **No feature → feature dependency.** A feature compiles and tests in isolation with
  only `:domain`/`:core-utils` on its classpath.
- **Consumer owns the port.** The interface expresses exactly what the consumer needs
  (interface segregation), e.g. `booking.UserGateway.findPushNotificationToken(userId)`
  vs `fitness-class.UserGateway.findAllPushNotificationTokens()`.
- **`:app` is the only place that knows every feature.** Swapping a provider (e.g. a new
  APNS notifier) is an adapter change in `:app`, invisible to the consumer.