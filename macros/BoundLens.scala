package ai.x.lens

import scala.reflect.macros.blackbox.Context
import scala.language.dynamics
import scala.language.experimental.macros
import monocle._
import monocle.syntax.ApplyLens

object `package`{
  implicit class ImplicitBoundLens[T]( v: T ) {
    def lens = BoundLens( v )
  }
}

/** Syntactic sugar around an ApplyLens */
class BoundLens[S, A]( val applyLens: ApplyLens[S, S, A, A] ) extends AnyVal {
  def apply[C]( field: A => C ): BoundLens[S, C] = macro BoundLensMacros.apply[S, A, C]
  def unmodified: S = applyLens.s
  def get: A = applyLens.get
  def set( value: A ): S = applyLens.set( value )
  def setIfDefined( value: Option[A] ): S = value.map( applyLens.set ) getOrElse unmodified
  def modify( diff: A => A ): S = applyLens.modify( diff )
  def modifyIfDefined( diff: Option[A => A] ): S = diff.map( applyLens.modify ) getOrElse unmodified
}

object BoundLens {
  def apply[S]( value: S ) = new BoundLens( ApplyLens[S, S, S, S]( value, Lens.id ) )
}

object BoundLensMacros {
  def apply[S, A: c.WeakTypeTag, C]( c: Context )( field: c.Expr[A => C] ) = {
    import c.universe._
    c.Expr[BoundLens[S, C]]( q"""
      new _root_.ai.x.lens.BoundLens(
        ${c.prefix.tree}.applyLens composeLens
          _root_.monocle.macros.GenLens[${c.weakTypeOf[A]}](${field})
      )
    """ )
  }

  def update[S, A, C]( c: Context )( field: c.Expr[A => C], value: c.Expr[C] ) = {
    import c.universe._
    c.Expr[S]( q"${c.prefix.tree}($field).set($value)" )
  }
}
