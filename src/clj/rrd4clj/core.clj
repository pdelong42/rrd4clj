(ns rrd4clj.core
  (:refer-clojure :exclude (deftype))
  (:use rrd4clj.imports)
  (:use [clojure.contrib.types :only (deftype defadt match)])
  (:use funky)
  (:import java.io.File))

(import-all)

;;
;; Private utilities
;;
(defmulti add-elem
  {:private true}
  (fn [_ elem] (class elem)))

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

(defadt ::rrd
  (created-rrd rrd-def factory)
  (opened-rrd path read-only factory)
  (imported-rrd path external-path factory))

;;
;; Public APIs
;;

(defn #^DsDef data-source
 "Creates new data source definition object"
  [#^String ds-name
   #^DsType ds-type
   #^long heartbeat
   #^double min-value
   #^double max-value]
 (DsDef. ds-name ds-type heartbeat min-value max-value))

(defn #^ArcDef rr-archive
  "Creates new round-robin archive definition object"
  [#^ConsolFun consol-fun
   #^double xff
   #^int steps
   #^int rows]
  (ArcDef. consol-fun xff steps rows))

(def
  #^{:doc "Creates new RRD definition object"
     :arglists '([path :start-time time :step step & ds&raa])}
  rrd-def
  (fnk [path
        :start-time nil
        :step RrdDef/DEFAULT_STEP
        & more]
    (let [rrd-def (if start-time
                    (RrdDef. path start-time step)
                    (RrdDef. path step))]
      (doseq [x more] (add-elem rrd-def x))
      rrd-def)))

(defn #^::rrd create
  "Creates new RRD object"
  ([#^RrdDef rrd-def]
     (created-rrd rrd-def nil))
  ([#^RrdDef rrd-def #^RrdBackendFactory factory]
     (created-rrd rrd-def factory)))

(defn #^::rrd open
  "Opens the existing RRD object"
  ([#^String path]
     (opened-rrd path false nil))
  ([#^String path second]
     (if (instance? Boolean second)
       (opened-rrd path second nil)
       (opened-rrd path false second)))
  ([#^String path read-only factory]
     (opened-rrd path read-only factory)))

(defn #^::rrd create-or-open
  "Creates if specified rrd doesn't exists, or opens it"
  ([#^RrdDef rrd-def]
     (let [path (.getPath rrd-def)]
       (if (.exists (File. path))
         (open path)
         (create rrd-def))))
  ([#^RrdDef rrd-def #^RrdBackendFactory factory]
     (let [path (.getPath rrd-def)]
       (if (.exists (File. path))
         (open path)
         (create rrd-def)))))

(defn #^::rrd import-to
  "Imports RRD or XML and copy it to new RRD object"
  ([#^String path #^String external-path]
     (imported-rrd path external-path nil))
  ([#^String path #^String external-path #^RrdBackendFactory factory]
     (imported-rrd path external-path factory)))

(defn sample [#^long time & values]
  "Creates new sampling object"
  {:time time, :values values})
