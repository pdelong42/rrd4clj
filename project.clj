(defproject org.clojars.maoe/rrd4clj "0.0.0-SNAPSHOT"
   :description "RRD API for Clojure"
   :dependencies
   [  [org.clojure/clojure         "1.7.0"]
      [org.clojure/clojure-contrib "1.2.0"]
      [org.rrd4j/rrd4j             "2.2.1"]  ]
   :main rrd4clj.examples
   :namespaces [rrd4clj.examples rrd4clj.core rrd4clj.io]  )
