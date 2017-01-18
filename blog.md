# Generating parsers with Scala macros

Macros are functions that run at compile time to generate code. [Scala macros](http://scalamacros.org/) are written in Scala itself using an API that lets you inspect and create [ASTs](https://en.wikipedia.org/wiki/Abstract_syntax_tree). Using [quasiquotes](http://docs.scala-lang.org/overviews/quasiquotes/intro.html), this can be as easy as inserting a value into a code template. Vanilla Scala only supports [def macros](http://docs.scala-lang.org/overviews/macros/overview), which let you generate methods. With the [macro paradise compiler plugin](http://docs.scala-lang.org/overviews/macros/paradise) you can also generate or modify classes and objects. For a good overview of macro use cases, have a look at [Eugene Burmako's "What Are Macros Good For?" slides](http://scalamacros.org/paperstalks/2013-07-17-WhatAreMacrosGoodFor.pdf). In this post, I will not cover all possible uses of Scala macros or give a comprehensive overview. Instead, I will walk you through a solution to a specific problem: Generating parsers for custom file formats.

## The problem: Parsing energy industry flows

### D188 file format, sample dataset
### Specification of file formats: rows, field types, repeating elements etc
### Why not write parsers manually? Tedious, error prone, DRY

## Solution - generate case classes and parsers with a macro annotation

### Project setup: separate compilation runs, IDE issues
### File format spec
### Generating case classes
### Generating parsers
### Problems and future improvements

## Summary
