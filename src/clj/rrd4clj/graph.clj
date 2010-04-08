(ns rrd4clj.graph
  (:use [rrd4clj core imports])
  (:use funky)
  (:use [clojure.contrib seq-utils])
  (:import [org.rrd4j.graph RrdGraphDef]
           [java.awt Color Font]))

(defprotocol GraphElement
  "hogefuga"
  (add [x gr] "bakaaho"))

(deftype area [name color legend]
  GraphElement
  (add [x gr] (.area gr name color legend)))

(deftype line [name color legend]
  GraphElement
  (add [x gr] (.line gr name color legend)))

(deftype stack [name color legend]
  GraphElement
  (add [x gr] (.stack gr name color legend)))

(deftype gr-data-source [name rrd-path ds-name consol-fun]
  GraphElement
  (add [x gr] (.datasource gr name rrd-path ds-name consol-fun)))

(deftype gr-cdef-source [name reverse-polish-notation]
  GraphElement
  (add [x gr] (.datasource gr name reverse-polish-notation)))

(defn stack-of [elem & more]
  (let [follows (map #(stack (:name %) (:color %) (:legend %)) more)]
    (cons elem follows)))

(def
  #^{:doc "Creates a new RRD Graph definition obejct"
     :arglists '()}
  graph
  (fnk [path
        :start-time RrdGraphDef/DEFAULT_START
        :end-time RrdGraphDef/DEFAULT_END
        :pool-used true
        :time-axis nil
        :value-axis nil
        :alt-y-grid false
        :no-minor-grid false
        :alt-y-mrtg false
        :alt-auto-scale false
        :alt-auto-scale-max false
        :units-exponent false
        :units-length RrdGraphDef/DEFAULT_UNITS_LENGTH
        :vertical-label ""
        :width RrdGraphDef/DEFAULT_WIDTH
        :height RrdGraphDef/DEFAULT_HEIGHT
        :interlaced false
        :image-info nil
        :image-format RrdGraphDef/DEFAULT_IMAGE_FORMAT
        :background-image nil
        :overlay-image nil
        :unit nil
        :lazy false
        :min-value nil
        :max-value nil
        :rigid false
        :base RrdGraphDef/DEFAULT_BASE
        :logarithmic false
        :color nil
        :no-legend false
        :only-graph false
        :force-rules-legend false
        :title ""
        :step nil
        :small-font nil
        :large-font nil
        :draw-x-grid true
        :draw-y-grid true
        :image-quality RrdGraphDef/DEFAULT_IMAGE_QUALITY
        :anti-aliasing true
        :text-anti-aliasing true
        :show-signature true
        :first-day-of-week nil
        & more]
    (let [gr-def (RrdGraphDef.)]
      (doto gr-def
        (.setFilename path)
        (.setTimeSpan start-time end-time)
        (.setAntiAliasing anti-aliasing)
        (.setTextAntiAliasing text-anti-aliasing)
        (.setTitle title)
        (.setHeight height)
        (.setWidth width)
        (if small-font (.setSmallFont small-font))
        (if large-font (.setLargeFont large-font))
        (.setImageFormat image-format))
      (doseq [x (flatten more)]
        (println x)
        (add x gr-def))
      gr-def)))
