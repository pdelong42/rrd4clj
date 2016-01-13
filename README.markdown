rrd4clj
==============

RRD API for Clojure using RRD4J.

Installation
-------------

To include rrd4clj in your Leiningen project, add the following
dependency to your ``project.clj``:

    [org.clojars.maoe/rrd4clj "0.0.0-SNAPSHOT"]

Warning: the above is an old version of the code.  Ownership of this
repository has recently changed hands to me, and I need to make time
to push the latest version of the code to clojars.  Apologies for the
delays - I'll get to it as free-time permits.

Documentation
-------------

- [API Reference](http://maoe.github.com/rrd4clj/autodoc/)
- [MinMax demo](http://github.com/maoe/rrd4clj/blob/master/src/clj/rrd4clj/examples.clj)

License
-------------

Copyright (c) 2010-2015 Mitsutoshi Aoe and released under New BSD Lisence.

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

 - [DONE] ask Mitsutoshi if he's amenable to changing the license
   and/or dropping his copyright (I'm assuming he doesn't want to
   maintain the code any more, but I'll try a pull-request anyway -
   can't hurt).
   - Mitsutoshi merged the pull-req and transferred ownership to me.
