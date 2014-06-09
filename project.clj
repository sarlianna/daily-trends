(defproject trends "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"] 
                 [org.clojure/clojurescript "0.0-2202"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/tools.trace "0.7.8"]
                 [om "0.5.0"]
                 [fogus/ring-edn "0.2.0"]
                 [ring "1.2.2"]
                 [garden "1.1.6"]
                 [compojure "1.1.8"]
                 [clj-stacktrace "0.2.7"]
                 [com.datomic/datomic-free "0.9.4766.16"]
                 [sablono "0.2.16"]]

  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-ring "0.8.10"]]
  :source-paths ["src/app" "src/server"]
  :ring {:handler core/app}
  :cljsbuild { :builds [{:id "dev"
                         :source-paths ["src/app"]
                         :compiler {:externs ["react/react-with-addons.min.js"]
                                    :closure-warnings {:externs-validation :off
                                                       :non-standard-jsdoc :off}
                                    :output-to "./resources/public/dist/app.js"
                                    :output-dir "./resources/public/dist/out"
                                    :optimizations :none
                                    :source-map true}}]}
  :injections [(let [orig (ns-resolve (doto
                                        'clojure.stacktrace require)
                                        'print-cause-trace)
                     new (ns-resolve (doto
                                       'clj-stacktrace.repl require)
                                     'pst)]
                 (alter-var-root orig (constantly (deref new))))])
