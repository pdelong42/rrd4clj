(ns rrd4clj.core
  (:use rrd4clj.imports)
  (:import
     java.io.File
     [org.rrd4j.core ArcDef DsDef RrdDb RrdDef]  )  )

(import-statics)

;; uniform interface for DataSources and RoundRobinArchives
(defprotocol RRDElement (add [x rrd]))

(deftype DataSource
   [ds-name ds-type heartbeat min-value max-value]
   RRDElement
   (add
      [ds rrd]
      (.addDatasource rrd
         (DsDef.
            (.ds-name   ds)
            (.ds-type   ds)
            (.heartbeat ds)
            (.min-value ds)
            (.max-value ds)  )  )  )  )

(deftype RoundRobinArchive
   [consol-fn xff steps rows] 
   RRDElement
   (add
      [arc rrd]
      (.addArchive rrd
         (ArcDef.
            (.consol-fn arc)
            (.xff       arc)
            (.steps     arc)
            (.rows      arc)  )  )  )  )

;; public API
(def data-source DataSource)

(def round-robin-archive RoundRobinArchive)

(defn rrd_define
   #^{:doc "Creates new RRD definition object"
      :arglists '([path :start-time time :step step & ds+raa])}
   [  path
      {  :keys [start-time step]
         :or
         {  start-time nil
            step       RrdDef/DEFAULT_STEP  }  }
      & ds+raa  ]
   (let
      [  rrd-def
         (if start-time
            (RrdDef. path start-time step)
            (RrdDef. path step)  )  ]
      (doseq
         [elem ds+raa]
         (add elem rrd-def)  )
      (RrdDb. rrd-def)  )  )

(defn sample
   [time & values]
   {:time time :values values}  )
