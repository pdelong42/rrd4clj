(ns rrd4clj.imports)

(defmacro import-statics []
  '(do
      (use 'clojure.contrib.import-static)
      (import-static org.rrd4j.ConsolFun AVERAGE FIRST LAST MAX MIN TOTAL)
      (import-static org.rrd4j.DsType ABSOLUTE COUNTER DERIVE GAUGE)
      (import-static org.rrd4j.core.Util getTimestamp getTime)  )  )
