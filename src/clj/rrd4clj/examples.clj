(ns rrd4clj.examples
  (:use [rrd4clj core io graph])
  (:use clojure.contrib.duck-streams
        clojure.contrib.import-static)
  (:import [java.io File]
           [java.awt Color Font])
  (:gen-class))

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
        rrd-path (demo-path "minmax.rrd")
        graph-path (demo-path "minmax.png")]
    ;; create
    (with-rrd [rrd (create (rrd-def rrd-path
                             :start-time (- start 1)
                             :step 300
                             (data-source "a" GAUGE 600 Double/NaN Double/NaN)
                             (rr-archive AVERAGE 0.5 1 300)
                             (rr-archive MIN 0.5 12 300)
                             (rr-archive MAX 0.5 12 300)))]
      ;; update
      (doseq [t (range start end 300)]
        (update rrd
          (sample t (+ 50 (* 50 (Math/sin (/ t 3000.0)))))))

      ;; fetch
      ;; (println
      ;;   (fetch rrd AVERAGE start end))

      ;; graph
      (let [gr (graph graph-path
                      :width 450
                      :height 250
                      :image-format "PNG"
                      :start-time start
                      :end-time (+ start 86400)
                      :title "rrd4clj's MINMAX demo"
                      :large-font (Font. "Consolas" Font/PLAIN 12)
                      :small-font (Font. "Consolas" Font/PLAIN 10)
                      :anti-aliasing true
                      (gr-data-source "a" rrd-path "a" AVERAGE)
                      (gr-data-source "b" rrd-path "a" MIN)
                      (gr-data-source "c" rrd-path "a" MAX)
                      (area "a" (Color/decode "0xb6e4") "real")
                      (line "b" (Color/decode "0x22e9") "min")
                      (line "c" (Color/decode "0xee22") "max"))]
        (draw gr)
        ))))

(defn -main [] (min-max-demo))
