# Change Log
All notable changes to this project will be documented in this file.

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
Released on 2018-07-05. This is the initial version.
