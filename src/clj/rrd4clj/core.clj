(ns rrd4clj.core
  (:use rrd4clj.imports
        funky)
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

(defprotocol Rrd
  (instantiate [this]))

(deftype ExistingRrd [path read-only?] Rrd
  (instantiate [this] (RrdDb. path read-only?)))

(deftype ImportedRrd [path external-path] Rrd
  (instantiate [this] (RrdDb. path external-path)))

(deftype NewRrd [rrd-def] Rrd
  (instantiate [this]
    (let [path (.getPath rrd-def)]
      (if (.exists (File. path))
        (instantiate (ExistingRrd path false))
        (RrdDb. rrd-def)))))

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

(defn sample [#^long time & values]
  "Creates new sampling object"
  {:time time, :values values})
