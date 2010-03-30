(ns rrd4clj.io
  (:refer-clojure :exclude [deftype])
  (:use rrd4clj.core
        rrd4clj.imports)
  (:use clojure.contrib.types)
  )

(import-all)

;; Private utilities
(defn- #^RrdDb instantiate-rrd
  "Opens the RRD object which already exists on disks
   or creates new RRD object"
  [rrd]
  {:pre [(= (type rrd) ::rrd)]}
  (match rrd
    (created-rrd d f)    (if (nil? f) (RrdDb. d)   (RrdDb. d f))
    (opend-rrd p r f)    (if (nil? f) (RrdDb. p r) (RrdDb. p r f))
    (imported-rrd p e f) (if (nil? f) (RrdDb. p e) (RrdDb. p e f))))

(defn- get-sample [rrd]
  (.createSample rrd))

(defn- add-sample [sample val]
  (doto sample
    (.setTime (:time val))
    (.setValues (into-array Double/TYPE
                            (:values val)))))

;;
;; Public APIs
;;

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
                             `(let [~sym (instantiate-rrd ~rrd-obj)]
                                (try (with-rrd ~(vec rest) ~@body)
                                     (finally
                                      (.close ~(bindings 0))))))
    :else (throw (IllegalArgumentException.
                   "with-rrd only allows Symbols in bindings"))))

(defn update
  "Updates RRD"
  [rrd & samples]
  (let [smpl (get-sample rrd)]
    (doseq [s samples]
      (add-sample smpl s))
    (.update smpl)))

(defn fetch
  "Fetches data from RRD"
  [#^RrdDb rrd
   #^ConsolFun consol-fn
   #^long start-time
   #^long end-time]
  (.fetchData
   (.createFetchRequest rrd consol-fn start-time end-time)))
