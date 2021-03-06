(ns rrd4clj.core-test
  (:use [rrd4clj.core] :reload-all)
  (:use rrd4clj.imports)
  (:use [clojure.test]
        [clojure.contrib.import-static])
  (:import [org.rrd4j.core DsDef ArcDef]))

(import-static org.rrd4j.ConsolFun AVERAGE FIRST LAST MAX MIN TOTAL)
(import-static org.rrd4j.DsType ABSOLUTE COUNTER DERIVE GAUGE)

(deftest test-data-source
  (let [ds (DsDef. "name" COUNTER 300 0 Double/NaN)]
    (are [expr answer] (= expr answer)
      (class ds) DsDef)))

(deftest test-rr-archive
  (let [rra (ArcDef. AVERAGE 0.5 1 600)]
    (are [expr answer] (= expr answer)
      (class rra) ArcDef)))
