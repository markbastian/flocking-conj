(defproject
  flocking-conj "0.1.0-SNAPSHOT"
  :description "An implementation of flocking behaviors in Clojure, modified for presentation at Clojure/conj 2015."
  :url "https://github.com/markbastian/flocking-conj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.58"]
                 [numerics "0.1.0-SNAPSHOT"]
                 [quil "2.2.6"]]

  ;:main flocking.launcher

  :jar-exclusions [#"\.swp|\.swo|\.DS_Store"]
  :profiles {:uberjar {:aot :all}
             :dev {:plugins [[lein-cljsbuild "1.0.6"]
                             [org.clojure/clojurescript "1.7.58"]]}
             :cljs {:plugins [[lein-cljsbuild "1.0.6"]] }}

  :source-paths ["src/clj" "src/cljc"]

  :clj {:builds [{ :source-paths ["src/clj" "src/cljc" "test"] }]}

  :cljsbuild {:builds [{ :source-paths ["src/cljs" "src/cljc"]
                        :compiler { :output-to "resources/public/js/flocking-conj.js"
                                   :optimizations :advanced
                                   :pretty-print true}}]})
