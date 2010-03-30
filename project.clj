(defproject org.clojars.maoe/rrd4clj "0.0.0-SNAPSHOT"
  :description "RRD API for Clojure"
  :dependencies [[org.clojure/clojure "1.2.0-master-SNAPSHOT"]
                 [org.clojure/clojure-contrib "1.2.0-master-SNAPSHOT"]
                 [de.huxhorn.lilith/de.huxhorn.lilith.3rdparty.rrd4j "2.0.5"]
                 [org.clojars.maoe/funky "0.0.6"]]
  :dev-dependencies [[leiningen/lein-swank "1.1.0"]
                     [lein-clojars "0.5.0-SNAPSHOT"]
                     [autodoc "0.7.0"]]
  :namespaces [rrd4clj.core rrd4clj.io]
  )
