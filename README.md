# Experimental plugin for OpenSearch to list built-in Analyzers

Please see https://github.com/opensearch-project/OpenSearch/issues/5481

When this plugin is installed on OpenSearch node it adds a new REST endpoint `/_nodes/{nodeId}/analyzers` that returns complete list of all built-in `analyzers`, `tokenizers`, `tokenFilters`, `charFilters` and `normalizers` of that node (`/_nodes/analyzers` is a shorthand for all cluster nodes).

The output also includes a list of all AnalysisPlugin(s) available on that node listing all relevant analysis components introduced by each individual plugin.

This plugin is a WIP.

If this functionality will be found useful then it may find its way directly into OpenSearch "core" and this repository will be archived.

There is no other info available here atm.

### Running the tests

Now you can run all the tests like so:
```
./gradlew check
```

### Running testClusters with the plugin installed 
```
./gradlew run
```

Then you can see this plugin in action by running:
```
curl -XGET 'localhost:9200/_nodes/analyzers'
```

## License
This code is licensed under the Apache 2.0 License. See [LICENSE.txt](LICENSE.txt).
