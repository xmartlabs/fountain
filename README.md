# XlPaging
<p align="center">
<a href="https://github.com/xmartlabs/xlpaging/master/LICENSE"><img src="http://img.shields.io/badge/license-MIT-blue.svg?style=flat" alt="License: MIT" /></a>
</p>

A smart and simple way to work with paged endpoints.

## Overview

Xlpaging is a Kotlin library conceived to make your life easier when dealing with paged endpoint services, where the paging is based on incremental page numbers (e.g. 1, 2, 3, ...).
It uses the [Google Android Architecture Components](https://developer.android.com/topic/libraries/architecture/), mainly the [Android Paging Library](https://developer.android.com/topic/libraries/architecture/paging/) to make easier to work with paged services.

The main goal of the library is to easily provide a [Listing](xlpagingbypagenumber/src/main/java/com/xmartlabs/xlpagingbypagenumber/Listing.kt) component from a common service specification.
[Listing](xlpagingbypagenumber/src/main/java/com/xmartlabs/xlpagingbypagenumber/Listing.kt) provides mainly five elements to take control of the paged list.

```kotlin
data class Listing<T>(
    /* the LiveData of paged lists for the UI to observe */
    val pagedList: LiveData<PagedList<T>>,
    /* represents the network request status to show to the user */
    val networkState: LiveData<NetworkState>,
    /* represents the refresh status to show to the user. */
    val refreshState: LiveData<NetworkState>,
    /* refreshes the whole data and fetches it from scratch. */
    val refresh: () -> Unit,
    /* retries any failed requests. */
    val retry: () -> Unit
)
```

Basically, you could manage all data stream with a `Listing` component, which is awesome!
It's really flexible and useful to display the paged list entities and reflect the network status changes in the UI.

XlPaging provides two ways to generate a `Listing` component from paged services:    
1. **Network support:** Provides a `Listing` based on a service that uses Retrofit and RxJava. Note entities won't be saved in memory nor disk.
1. **Cache + Network support:** Provides a `Listing` with cache support using for service based on Retrofit and RxJava, and a [`DataSource`](https://developer.android.com/reference/android/arch/paging/DataSource) for caching the data.
We recommend you to use [Room](https://developer.android.com/topic/libraries/architecture/room) to provide the `DataSource`, because it will be easier. However, you could use any other `DataSource`.

## Download

Add library to project dependencies with JitPack.
```groovy
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation '' //todo: Complete with the right package
}
```

## Usage
You should read the documentation to use it, but the `Listing` can be provided by two methods.

### Network support
You can read the documentation [here]().

The `Listing` with network support can be obtained using:
```kotlin
XlPaging.createNetworkListing(pagingHandler = pagingHandler)
```

There's only one required structure, `PagingHandler<ListResponse<T>>`, which this library uses to handle the paging.
There are two methods: one to check if a page could be fetched and another one to fetch it.
```kotlin
interface PageFetcher<T> {
  @CheckResult
  fun fetchPage(page: Int, pageSize: Int): Single<out T>
}

interface PagingHandler<T> : PageFetcher<T> {
  @CheckResult
  fun canFetch(page: Int, pageSize: Int): Boolean
}
```

### Cache + Network support

The `Listing` with network and cache support can be obtained using:

```kotlin
XlPaging.createNetworkWithCacheSupportListing(
    dataSourceEntityHandler = dataSourceEntityHandler,
    dataSourceFactory = dataSourceFactory,
    pagingHandler = pagingHandler
)
```
There're three required components 
1. A `PageHandler` to fetch all pages.
1. A [`DataSource.Factory<*, Value>`](https://developer.android.com/reference/android/arch/paging/DataSource.Factory) to setup the [`LivePagedListBuilder`](https://developer.android.com/reference/android/arch/paging/LivePagedListBuilder) of the [Android Paging Library](https://developer.android.com/topic/libraries/architecture/paging/) and get a `LiveData<PagedList<Value>>`.
1. A `DataSourceEntityHandler` to update the `DataSource`.
It's the interface that the library will use to take control of the `DataSource`.

```kotlin
interface DataSourceEntityHandler<T> {
  @WorkerThread
  fun saveEntities(response: T?)

  @WorkerThread
  fun dropEntities()

  @WorkerThread
  fun runInTransaction(transaction: () -> Unit)
}
```
It has only three methods that you should implement:
- `runInTransaction` will be used to apply multiple `DataSource` operations in a single transaction. That means that if something fails, all operations will fail.
- `saveEntities` will be invoked to save all entities into the `DataSource`.
This will be executed in a transaction.
- `dropEntities` will be used to delete all cached entities from the `DataSource`.
This will be executed in a transaction.

## Library status
Done:
- [x] Define and write the Library API.
- [x] Write sample app.

Current:
- [ ] Add documentation in each public class.
- [ ] Write documentation.
- [ ] Generate test.

## Getting involved

* If you **want to contribute** please feel free to **submit pull requests**.
* If you **have a feature request** please **open an issue**.
* If you **found a bug** check older issues before submitting a new one.
* If you **need help** or would like to **ask a general question**, use [StackOverflow]. (Tag `xmart-recyclerview`).

**Before contributing, please check the [CONTRIBUTING](CONTRIBUTING.md) file.**

## Changelog

The changelog for this project can be found in the [CHANGELOG](CHANGELOG.md) file.

## About
Made with ❤️ by [XMARTLABS](http://xmartlabs.com)
