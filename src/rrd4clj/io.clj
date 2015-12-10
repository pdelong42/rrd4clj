(ns rrd4clj.io
   (:use rrd4clj.core rrd4clj.imports)
   (:import
      [java.io File IOException]
      [java.lang IllegalArgumentException]  )  )

(import-all)

;; (defn fetch
;;   "Fetches data from RRD"
;;   [#^RrdDb rrd
;;    #^ConsolFun consol-fn
;;    #^long start-time
;;    #^long end-time]
;;   (.fetchData
;;    (.createFetchRequest rrd consol-fn start-time end-time)))

;; new API
(defprotocol RRD
  (instantiate [x]))

(deftype OpenRRD [path read-only?]
  RRD
  (instantiate [x]
    (RrdDb. path read-only?)))

(deftype CreateRRD [rrd-def]
  RRD
  (instantiate [x]
    (let [path (.getPath rrd-def)]
      (if (.exists (File. path))
        (RrdDb. path false)
        (RrdDb. rrd-def)))))

(deftype ImportRRD [path external-path]
  RRD
  (instantiate [x]
    (RrdDb. path external-path)))

;; io/open
(defn open
  ([path]
     (instantiate (->OpenRRD path false)))
  ([path read-only?]
     (instantiate (->OpenRRD path read-only?))))

;; io/create
(def create
  (comp instantiate CreateRRD))

;; io/update
(defn update_rrd
  "Updates RRD"
  [rrd & samples]
  (doseq [s samples]
    (try (doto (.createSample rrd)
           (.setTime (:time s))
           (.setValues (into-array Double/TYPE (:values s)))
           (.update))
         (catch IllegalArgumentException e
             (println "invalid arguments in setValues" e))
         (catch IOException e
           (println "io error in update" e)))))

;; io/import
;; io/graph
(defn graph
  "Draws a graph"
  [#^RrdGraphDef gr]
  (RrdGraph. gr))

;;  Graph
;;   DataSource
;;   CDefSource
;;   Area
;;   Line
;;   Stack
;; io/fetch
;;   Fetch

;; io/tune
;;   Tune
;; io/last
;;   Last
;; io/info
;;   Info
;; io/xport
;;   Export

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
                             `(let [~sym (create ~rrd-obj)]
                                (try (with-rrd ~(vec rest) ~@body)
                                     (finally
                                      (.close ~(bindings 0))))))
    :else (throw (IllegalArgumentException.
                   "with-rrd only allows Symbols in bindings"))))
