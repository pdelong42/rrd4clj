(ns rrd4clj.examples
   (:use
      rrd4clj.core
      rrd4clj.imports  )
   (:require
      [rrd4clj.io    :as io]
      [rrd4clj.graph :as  g]  )
   (:import
      [java.awt Color Font]
      java.io.File
      org.rrd4j.core.RrdDb  )
   (:gen-class)  )

(import-statics)

(defn demo-dir [] ; rename this to be different from the lexical below
   (let
      [  home-dir (File. (System/getProperty "user.home"))
         demo-dir (File. (format "%s%srrd4clj-demo" home-dir File/separator))  ]
      (when-not
         (.exists demo-dir)
         (.mkdir  demo-dir)  )
      demo-dir  )  )

(defn demo-path
   [file]
   (format "%s%s%s" (demo-dir) File/separator file)  )

(defn min-max-demo
   [start end rrd-path graph-path]
   (let ; this line originally used io/with-rrd
      [  rrdi
         (rrd_define
            rrd-path
            {  :start-time (dec start)
               :step       300  }
            (->DataSource "a" GAUGE 600 Double/NaN Double/NaN)
            (->RoundRobinArchive AVERAGE 0.5  1 300)
            (->RoundRobinArchive MIN     0.5 12 300)
            (->RoundRobinArchive MAX     0.5 12 300)  )  ]

      (apply io/rrd_update rrdi
         (for
            [t (range start end 300)]
            (sample t (+ 50 (* 50 (Math/sin (/ t 3000.0)))))  )  )

      (io/rrd_graph
         (g/graph
            graph-path
            {  :width 450
               :height 250
               :image-format "PNG"
               :start-time start
               :end-time (+ start 86400)
               :title "rrd4clj's MINMAX demo"
               :anti-aliasing false  }
            (g/->DataSource "a" rrd-path "a" AVERAGE)
            (g/->DataSource "b" rrd-path "a" MIN)
            (g/->DataSource "c" rrd-path "a" MAX)
            (g/->CDefSource "d" "a,-1,*")
            (g/->Area "a" (Color/decode "0xb6e4") "real")
            (g/->Line "b" (Color/decode "0x22e9") "min")
            (g/->Line "c" (Color/decode "0xee22") "max")
            (g/stack-of
               (g/->Area "d" (Color/decode "0xb6e4") "inv")
               (g/->Area "d" (Color/decode "0xfffe") "stack")
               (g/->Area "d" (Color/decode "0xeffe") "stack2")  )  )  )  )  )

(defn -main []
   (let
      [  now (getTime)  ]
      (min-max-demo
         now
         (+ now (* 300 300))
         (demo-path "minmax.rrd")
         (demo-path "minmax.png")  )  )  )
