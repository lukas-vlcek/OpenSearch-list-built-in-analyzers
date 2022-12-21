# Experimental plugin for OpenSearch to list built-in Analyzers

Please see https://github.com/opensearch-project/OpenSearch/issues/5481

This plugin is WIP and if this functionality will be found useful then most likely it will be part of the OpenSearch "core" and this repository will be deleted.

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

Then you can see that your plugin has been installed by running: 
```
curl -XGET 'localhost:9200/_cat/plugins'
```

## License
This code is licensed under the Apache 2.0 License. See [LICENSE.txt](LICENSE.txt).
