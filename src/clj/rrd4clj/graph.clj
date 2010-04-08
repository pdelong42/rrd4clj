(ns rrd4clj.graph
  (:use [rrd4clj core imports])
  (:use funky)
  (:import [org.rrd4j.graph RrdGraphDef]
           [java.awt Color Font]))

(defprotocol RrdGraphDefMember
  "hogefuga"
  (add [x gr] "bakaaho"))

(deftype area [name color legend]
  RrdGraphDefMember
  (add [x gr] (.area gr name color legend)))

(deftype line [name color legend]
  RrdGraphDefMember
  (add [x gr] (.line gr name color legend)))

(deftype gr-data-source [name rrd-path ds-name consol-fun]
  RrdGraphDefMember
  (add [x gr] (.datasource gr name rrd-path ds-name consol-fun)))

(deftype gr-cdef-source [name reverse-polish-notation]
  RrdGraphDefMember
  (add [x gr] (.datasource gr name reverse-polish-notation)))

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
      (doseq [x more] (add x gr-def))
      gr-def)))
