(ns rrd4clj.core
  (:refer-clojure :exclude (deftype))
  (:use clojure.contrib.import-static
        [clojure.contrib.types :only (deftype defadt match)])
  (:use funky)
  (:import [org.rrd4j.core RrdDef DsDef ArcDef RrdDb Util]))

(import-static org.rrd4j.ConsolFun AVERAGE FIRST LAST MAX MIN TOTAL)
(import-static org.rrd4j.DsType ABSOLUTE COUNTER DERIVE GAUGE)
(import-static org.rrd4j.core.Util getTimestamp)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Pure operations
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;; pure creation

(defmulti add-elem
  {:private true}
  (fn [_ content] (class content)))

(defmethod add-elem DsDef
  [this data-source]
  (.addDatasource this data-source))

(defmethod add-elem ArcDef
  [this arcive]
  (.addArchive this arcive))

(defmethod add-elem :default
  [this elems]
  (doseq [elem elems]
    (add-elem this elem)))

(defnk rrd-def
  [path
   :start-time nil
   :step RrdDef/DEFAULT_STEP
   & ds&raa]
  (let [rrd-def (if start-time
                  (RrdDef. path start-time step)
                  (RrdDef. path step))]
    (doseq [x ds&raa] (add-elem rrd-def x))
    rrd-def))

;; (defmacro #^RrdDef rrd-def
;;   "Creates new RRD definition object"
;;   {:arglists '([#^String path]
;;                [#^String path, #^long step]
;;                [#^String path, #^long start-time, #^long step]
;;                [#^String path, #^long start-time, #^long step, #^int version])}
;;   [& args]
;;   `(RrdDef. ~@args))

(defn #^DsDef data-source
 "Creates new data source definition object"
  [#^String ds-name
   #^DsType ds-type
   #^long heartbeat
   #^double min-value
   #^double max-value]
 (DsDef. ds-name ds-type heartbeat min-value max-value))

(defn #^ArcDef rr-archive
  "Creates new archive definition object"
  [#^ConsolFun consol-fun
   #^double xff
   #^int steps
   #^int rows]
  (ArcDef. consol-fun xff steps rows))

(defadt ::rrd
  (created-rrd rrd-def factory)
  (opend-rrd path read-only factory)
  (imported-rrd path external-path factory))

(defn #^::rrd create-rrd
  ([#^RrdDef rrd-def]
     (created-rrd rrd-def nil))
  ([#^RrdDef rrd-def #^RrdBackendFactory factory]
     (created-rrd rrd-def factory)))

(defn #^::rrd open-rrd
  ([#^String path]
     (opend-rrd path false nil))
  ([#^String path second]
     (if (= (class second) Boolean)
       (opend-rrd path second nil)
       (opend-rrd path false second)))
  ([#^String path read-only factory]
     (opend-rrd path read-only factory)))

(defn #^::rrd import-rrd
  ([#^String path #^String external-path]
     (imported-rrd path external-path nil))
  ([#^String path #^String external-path #^RrdBackendFactory factory]
     (imported-rrd path external-path factory)))

(defn #^RrdDb open-rrd
  "Opens the RRD object which already exists on disks
   or creates new RRD object"
  [rrd]
  {:pre [(= (type rrd) ::rrd)]}
  (match rrd
    (created-rrd d f)    (if (nil? f) (RrdDb. d)   (RrdDb. d f))
    (opend-rrd p r f)    (if (nil? f) (RrdDb. p r) (RrdDb. p r f))
    (imported-rrd p e f) (if (nil? f) (RrdDb. p e) (RrdDb. p e f))))

(defn #^DsDef default-ds
  [#^String name #^DsType ds-type]
  (data-source name ds-type 600 Double/NaN Double/NaN))

(defn #^ArcDef daily-raa [#^ConsolFun cf]
  (round-robin-archive cf 0.5 1 600))

(defn #^ArcDef weekly-raa [#^ConsolFun cf]
  (round-robin-archive cf 0.5 6 700))

(defn #^ArcDef monthly-raa [#^ConsolFun cf]
  (round-robin-archive cf 0.5 24 775))

(defn #^ArcDef yearly-raa [#^ConsolFun cf]
  (round-robin-archive cf 0.5 288 797))

(def default-rr-archives
  (for [consol-fn (list AVERAGE MAX MIN)
        raa-fn    (list daily-raa weekly-raa monthly-raa yearly-raa)]
    (raa-fn consol-fn)))

;; pure updates
(defn get-sample [rrd]
  (.createSample rrd))

(defn sample [] )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; I/O operations
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; creates with io
(defmacro with-rrd
  "bindings => [name init ...]

   Evaluates body in a try expression with names bound to the values
   if the inits, and a finally clause that calls (.close name) on each
   name in reverse order."
  [bindings & body]
  {:pre [(vector? bindings)
         (even? (count bindings))]}
  (cond
    (= (count bindings) 0) `(do ~@body)
    (symbol? (bindings 0)) (let [[sym rrd-obj & rest] bindings]
                             `(let [~sym (open-rrd ~rrd-obj)]
                                (try (with-rrd ~(vec rest) ~@body)
                                     (finally
                                      (.close ~(bindings 0))))))
    :else (throw (IllegalArgumentException.
                   "with-rrd only allows Symbols in bindings"))))

;; updates with io
(defmacro dosample [& samples]
  samples)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Tests
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-test []
  (with-rrd
    [rrd (create-rrd (rrd-def "/tmp/hoge.rrd"
                       (data-source "sun" GAUGE 600 0 Double/NaN)
                       (data-source "shade" GAUGE 600 0 Double/NaN)
                       default-rr-archives))]
    rrd))

;;
;; (with-rrd
;;   [rrd (create-rrd some-definition)
;;   (dosample rrd
;;     (sample t values)
;;     (sample t values)
;;     (sample t values)))
;;   
            
;; (defn update []
;;   (let [rrd-db (RrdDb. "/tmp/hoge.rrd")
;;         sample (.createSample rrd-db)]
;;     (doto sample
;;       (.setTime (getTimestamp 2003 4 1))
;;       (.setValue "sun" 0)
;;       (.setValue "shade" 1)
;;     )))
