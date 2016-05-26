# ring-proxy

This project is a fork of tailrecursion's [ring-proxy](https://github.com/tailrecursion/ring-proxy) middleware.
This project only adds a project.clj to be able to work with our internal libraries.

HTTP proxy [ring middleware](https://github.com/ring-clojure/ring/blob/a02518275a06835e4fdd1a3af59d7c4c0408d25b/SPEC#L12)
for Clojure web applications.

### Dependency

```clojure
[com.guaranteedrate/ring-proxy "3.0.0"]
```

### Example

Assuming your application's route handler is defined as `routes`, you
may add a proxied route with something like the following:

```clojure
(ns your-ns
  (:require [tailrecursion.ring-proxy :refer [wrap-proxy]]))

(def app
  (-> routes
      (wrap-proxy "/remote" "http://some.remote.server/remote")))
```

## License

Copyright © 2013 Alan Dipert and Micha Niskin

Distributed under the Eclipse Public License, the same as Clojure.
