(defproject org.clojars.maoe/rrd4clj "0.0.0-SNAPSHOT"
   :description "RRD API for Clojure"
   :dependencies
   [  [org.clojure/clojure      "1.7.0"]
      [org.clojure/clojure-contrib "1.2.0"]
;      [org.clojure/clojure "1.2.0-master-SNAPSHOT"]
;      [org.clojars.maoe/funky "0.0.6"]
      [berkeleydb/je "3.2.76"]
      [org.rrd4j/rrd4j "2.2.1"]
    ]
   :dev-dependencies [
      [leiningen/lein-swank "1.1.0"]
      [lein-clojars "0.5.0-SNAPSHOT"]
      [autodoc "0.7.0"]
;      [lein-javac "0.0.2-SNAPSHOT"]
                      ]
;   :source-path "src/clj"
;   :java-source-paths ["src/java"]
   :main rrd4clj.examples
   :namespaces [rrd4clj.examples rrd4clj.core rrd4clj.io]  )
