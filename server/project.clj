(defproject default "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/tools.trace "0.7.8"]
                 [ring "1.2.2"]
                 [fogus/ring-edn "0.2.0"]
                 [clj-stacktrace "0.2.7"]
                 [com.datomic/datomic-free "0.9.4766.16"]
                 [compojure "1.1.8"]]
  :plugins [[lein-ring "0.8.10"]]
  :source-paths ["src/server"]
  :ring {:handler core/app})
  :injections [(let [orig (ns-resolve (doto
                                        'clojure.stacktrace require)
                                        'print-cause-trace)
                     new (ns-resolve (doto
                                       'clj-stacktrace.repl require)
                                     'pst)]
                 (alter-var-root orig (constantly (deref new))))]
