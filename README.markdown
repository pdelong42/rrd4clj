rrd4clj
==============

RRD API for Clojure using RRD4J.

Installation
-------------

To include rrd4clj in your Leiningen project, add the following
dependency to your ``project.clj``:

    [org.clojars.pdelong/rrd4clj "1.0.2"]

Warning: the above is an old version of the code.  Ownership of this
repository has recently changed hands to me, and I need to make time
to push the latest version of the code to clojars.  Apologies for the
delays - I'll get to it as free-time permits.

Documentation
-------------

- [API Reference](http://maoe.github.com/rrd4clj/autodoc/)
- [MinMax demo](http://github.com/pdelong/rrd4clj/blob/master/src/clj/rrd4clj/examples.clj)

License
-------------

Copyright (c) 2010-2015 Mitsutoshi Aoe and released under New BSD Lisence.
Copyright (c) 2016 Paul DeLong and released under New BSD Lisence.

ToDo
-------------

 - update access to my clojars account, and upload the latest release

 - migrate away from clojure-contrib, since it's deprecated
   - mostly done, but still using import-static

 - figure-out what's wrong with "with-rrd"

 - clean-up the use/require statements

 - write more unit tests

 - figure-out how to generate the API reference (using either Autodoc,
   or some equivalent tool), and put it on a github-pages site
