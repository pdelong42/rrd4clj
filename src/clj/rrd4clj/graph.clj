(ns rrd4clj.graph
  (:use funky)
  (:use [clojure.contrib seq-utils])
  (:require [clojure.zip :as z])
  (:import [org.rrd4j.graph RrdGraphDef]
           [java.awt Color Font]))

(defprotocol GraphElement
  (add [x gr] "add"))

(defprotocol SourcedGraphElement
  (stack [x] "boolean"))

(deftype Stack [name color legend]
  GraphElement
  (add [x gr] (.stack gr name color legend))
  SourcedGraphElement
  (stack [x] x))

(deftype Area [name color legend]
  GraphElement
  (add [x gr] (.area gr name color legend))
  SourcedGraphElement
  (stack [x] (Stack name color legend)))

(deftype Line [name color legend]
  GraphElement
  (add [x gr] (.line gr name color legend))
  SourcedGraphElement
  (stack [x] (Stack name color legend)))

(deftype DataSource [name rrd-path ds-name consol-fun]
  GraphElement
  (add [x gr] (.datasource gr name rrd-path ds-name consol-fun)))

(deftype CDefSource [name reverse-polish-notation]
  GraphElement
  (add [x gr] (.datasource gr name reverse-polish-notation)))

(defn- sourced? [elem]
  (instance? rrd4clj.graph.SourcedGraphElement elem))

;; public API
(def area Area)
(def line Line)
(def data-source DataSource)
(def cdef-source CDefSource)

(defn stack-of [& elems]
  (let [z (z/seq-zip elems)]
    (loop [loc (z/next z) replace? false]
      (cond
        (z/end? loc) (z/root loc)
        replace?
          (if (sourced? (z/node loc))
            (recur (z/next (z/edit loc stack)) true)
            (recur (z/next loc) true))
        :else
          (if (sourced? (z/node loc))
            (recur (z/next loc) true)
            (recur (z/next loc) false))))))

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
