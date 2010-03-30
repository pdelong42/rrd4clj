(ns rrd4clj.imports
  (:use clojure.contrib.import-static))

(defn import-all []
  (import '[org.rrd4j.core ArcDef DsDef RrdDb RrdDef Sample])
  (import-static org.rrd4j.ConsolFun AVERAGE FIRST LAST MAX MIN TOTAL)
  (import-static org.rrd4j.DsType ABSOLUTE COUNTER DERIVE GAUGE))
