(defproject app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"] 
                 [org.clojure/clojurescript "0.0-2202"]
                 [om "0.5.0"]]

  :main ^:skip-aot app.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :plugins [[lein-cljsbuild "1.0.3"]]
  :cljsbuild {
               :builds [{
                         :source-paths ["src-cljs"]
                         :compiler {
                                    :output-to "war/javascripts/main.js"
                                    :optimizations :whitespace
                                    :pretty-print true}}]}
            )
