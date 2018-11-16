# Change Log
All notable changes to this project will be documented in this file.

### [0.5.0](https://github.com/xmartlabs/fountain/releases/tag/0.5.0)
Released on 2018-11-16.
- Upgrade kotlin version to 1.3 and coroutines version to 1.0.1.
- Upgrade Android target version to 28

### [0.4.0](https://github.com/xmartlabs/fountain/releases/tag/0.4.0)
Released on 2018-10-23.
**Big Breaking changes:**
#### Fountain modules

The library now has 3 different modules:
- Coroutine module: Uses a [Coroutine Retrofit adapter](https://github.com/JakeWharton/retrofit2-kotlin-coroutines-adapter) to fetch the pages.
- Retrofit module: Uses a simple Retrofit [call](https://square.github.io/retrofit/2.x/retrofit/retrofit2/Call.html) to fetch the pages.
- RxJava2 module: Uses a RxJava2 Retrofit adapter to fetch the pages.

_These modules are independent and none of them are strictly required._
The new dependencies are:
```groovy
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    // This dependency is required only if you want to use a Retrofit service without a special adapter. 
    implementation 'com.github.xmartlabs.fountain:fountain-retrofit:0.4.0'

    // This dependency is required only if you want to use a Coroutine retrofit adapter.
    implementation 'com.github.xmartlabs.fountain:fountain-coroutines:0.4.0'

    // This dependency is required only if you want to use a RxJava2 retrofit adapter.
    implementation 'com.github.xmartlabs.fountain:fountain-rx2:0.4.0'
}
```

#### Not paged endpoint support
Although Fountain was designed to work with paged endpoints, it also supports working with not paged endpoints.
That means that you can use all [Listing] features in services that return a list that's not paged.

#### Factory constructors
There's one static factory object class for each each dependency.
- FountainCoroutines: Used to get a [Listing] from a Retrofit service which uses a Coroutine adapter.
- FountainRetrofit: Used to get a [Listing] from a Retrofit service without using a special adapter.
- FountainRx: Used to get a [Listing] from a Retrofit service which uses a RxJava2 adapter.

Each static factory has the same constructors with different params:
- `createNetworkListing`: A constructor to get a `Listing` component from a common **paged** Retrofit **service** implementation.
- `createNotPagedNetworkListing`: A constructor to get a `Listing` component from a common **not paged** Retrofit **service** implementation.
- `createNetworkWithCacheSupportListing`: A constructor to get a `Listing` component with **cache support** from a common **paged** Retrofit **service** implementation.
- `createNotPagedNetworkWithCacheSupportListing`: A constructor to get a `Listing` component with **cache support** from a common **not paged** Retrofit **service** implementation.

#### NetworkDataSourceAdapter creators

If you use either `ListResponseWithPageCount` or `ListResponseWithEntityCount` you can convert a `PageFetcher` to a `NetworkDataSourceAdapter` using these extensions:
```kotlin
fun <ServiceResponse : ListResponseWithEntityCount<*>>
    PageFetcher<ServiceResponse>.toTotalEntityCountNetworkDataSourceAdapter(firstPage: Int)
fun <ServiceResponse : ListResponseWithPageCount<*>>
    PageFetcher<ServiceResponse>.toTotalPageCountNetworkDataSourceAdapter(firstPage: Int)
```

_To more about these changes you can read the full documentation [here](https://xmartlabs.gitbook.io/fountain/)._

### [0.3.0](https://github.com/xmartlabs/fountain/releases/tag/0.3.0)
Released on 2018-09-12.
- Improve `NetworkState` structure [#28](https://github.com/xmartlabs/fountain/pull/28).
Now that structure has the requested `page` number, `pageSize`, `isFirstPage`, `isLastPage`.

**Breaking changes:**
The `NetworkState` data class is replaced by a `sealed class` and the `Status` enum was removed.

### [0.2.0](https://github.com/xmartlabs/fountain/releases/tag/0.2.0)
Released on 2018-07-23. 
- Added the possibility of using different models on Network and DataSource sources.
`Fountain.createNetworkWithCacheSupportListing` now requires two type declarations [#28](https://github.com/xmartlabs/fountain/pull/28).
- Fix issue when the initialization process throws an Exception [#29](https://github.com/xmartlabs/fountain/pull/29).

### [0.1.0](https://github.com/xmartlabs/fountain/releases/tag/0.1.0)
Released on 2018-07-05.
This is the initial version.
