# Generating parsers with Scala macros

When I first used [Scala macros](http://scalamacros.org/) a few years ago, they were still quite rough around the edges: It was tedious to create [ASTs](https://en.wikipedia.org/wiki/Abstract_syntax_tree) by hand, documentation was limited, and I even ran into [a compiler bug](https://issues.scala-lang.org/browse/SI-6155). But Scala macros have come a long way. [Quasiquotes](http://docs.scala-lang.org/overviews/quasiquotes/intro.html) make generating code in macros almost as easy as writing normal Scala code and documentation has improved, too. Only that bug is still open.
In this post, I will not cover all possible uses of Scala macros or give a comprehensive overview. Instead, I will walk you through a solution to a specific problem: Generating parsers for custom file formats.

## The problem: Parsing energy industry flows

Recently I've started working for Ovo Energy. In the energy industry, companies exchange data in a variety of file formats and since the energy industry has been around for a while, we're not using JSON or XML or any other (more or less) modern format. But all is not lost. Even though there are more than 200 different data formats, they are all described in a common way. Let's look at an example.

### D0188 data format

The "flow structure" table at https://dtc.mrasco.com/DataFlow.aspx?FlowCounter=0188&FlowVers=1&searchMockFlows=False describes two groups, "customer payment details" and "meter register detail per meter". The first group has 15 data items while the second group has 3. The "range" column tells us that for each D0188 file, there can be one to n entries of the first group, and for each such entry, there can be 1 to n instances of the second group. All file formats we're dealing with are text based and group entries are always represented as pipe separated values on a single line starting with the group number (in this case, 386 or 387). Here is some sample data:

```
386|44000000000001004000|20463|8100|20160922|20.00|S12A07953|17.25|0.00|0.00|0.00|20101101120000|20160919120000|759.00|-1.94|1200023528780|
387|1|8924.0|14.95|
386|44000000000002562000|347065|6948|20160922|10.00|S07X46677|17.23|0.00|0.00|0.00|20101101120000|20160919120000|11816.00|48.07|1413355260002|
387|1|59414.0|18.12|
387|2|38935.0|6.62|
```

In this example, we have two group 386s entries, the first one has one 387 entry and the second one has 2 387s.

### Let's just write some parsers?

If we just had a few of these data formats, creating custom case classes to hold the data and using parser combinators to create parsers for them would be a decent solution. But for large numbers of data formats this quickly becomes tedious and error prone. If you wanted to make changes to the way files are parsed, for example to the way errors are handled, you'd probably have to make those changes in all of your 200 parsers.

## Macros! Type providers!

With macros, we can use a pattern called [type providers](http://docs.scala-lang.org/overviews/macros/typeproviders.html), which you might know from F#. Type providers create classes based on information available at compile time. For example, there are type provider that create types from json schemas or relational database schemas. In Scala, you can use macro annotations for this task. This is what a macro annotation looks like:

```scala
class FlowParser extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro FlowParser.impl
}
```

This defines the macro annotation and points to the implementation.

```scala
object FlowParser {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    annottees.map(_.tree).toList match {
      case q"$mods object $name extends $parent with ..$parents { $self => ..$stats }" :: Nil =>
        q"$mods object $name extends $parent with ..$parents { $self => ..$stats }"
      case _ => c.abort(c.enclosingPosition, "Annotation @FlowParser can only be used with objects")
    }
  }
}
```

The implementation is a method that takes a `Context` and one or more `Expr`essions. If you annotate an object like this...

```scala
@FlowParser
object Foo {
  def bar = ???
}
```

... the AST of the object will be passed as an expression to the macro implementation. The macro can then use quasiquotes to pattern match on the annotated code and extract pieces of it such as the name of the object, its superclass, traits it is inheriting from and definitions in the class body. The return value of the macro is the expression the annotated class should be replaced with. In our example, the macro annotation simply puts the pieces of the object back together and returns it unchanged.

### File format spec

To give the macro something to do, we first need a description of the data formats we want to create parsers for.

```



```

### Generating case classes

### Generating parsers

## Problems with our solution

### IDE issues: three separate projects: macros, macro application, tests for classes created through macro application

### Deal with one-off issues like the format being too strict

## Could we do the same with Shapeless? Yes, but...

## Summary
