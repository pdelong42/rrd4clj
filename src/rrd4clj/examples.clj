(ns rrd4clj.examples
  (:use rrd4clj.core
        rrd4clj.io)
  (:use clojure.contrib.duck-streams
        clojure.contrib.import-static))

(import-static org.rrd4j.ConsolFun AVERAGE FIRST LAST MAX MIN TOTAL)
(import-static org.rrd4j.DsType ABSOLUTE COUNTER DERIVE GAUGE)
(import-static org.rrd4j.core.Util getTimestamp getTime)


(defn demo-dir []
  (let [home-dir (File. (System/getProperty "user.home"))
        demo-dir (File. (format "%s%srrd4clj-demo"
                                home-dir File/separator))]
    (when-not (.exists demo-dir) (.mkdir demo-dir))
    demo-dir))

(defn demo-path [file]
  (format "%s%s%s" (demo-dir) File/separator file))

(defn min-max-demo []
  (let [start (getTime), end (+ start (* 300 300))
        rrd-path (demo-path "minmax.rrd")]
    (with-rrd [rrd (create (rrd-def rrd-path
                             :start-time (- start 1)
                             :step 300
                             (data-source "a" GAUGE 600 Double/NaN Double/NaN)
                             (rr-archive AVERAGE 0.5 1 300)
                             (rr-archive MIN 0.5 12 300)
                             (rr-archive MAX 0.5 12 300)))]
      (doseq [t (range start end 300)]
        (update rrd
          (sample t (+ 50 (* 50 (Math/sin (/ t 3000.0))))))))))
