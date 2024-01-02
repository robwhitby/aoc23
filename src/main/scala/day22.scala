import scala.annotation.tailrec

object day22 {

  case class Point(x: Int, y: Int, z: Int)

  case class Brick(a: Point, b: Point, label: Char) {
    val points = (for {x <- a.x to b.x; y <- a.y to b.y; z <- a.z to b.z} yield Point(x, y, z)).toSet

    def moveDown(): Brick = Brick(a.copy(z = a.z-1), b.copy(z = b.z-1), label)
  }

  case class Stack(bricks: Set[Brick]) {
    lazy val points: Set[Point] = bricks.flatMap(_.points)

    def spaceFor(brick: Brick): Boolean = !brick.points.exists(points.contains)

    def moveBrick(brick: Brick): (Stack, Boolean) = {
      if (brick.a.z == 1) return (this, false)
      val moved = brick.moveDown()
      val otherBricks = Stack(bricks - brick)
      if (!otherBricks.spaceFor(moved)) {
        return (this, false)
      }
      (Stack(otherBricks.bricks + moved), true)
    }

    def fall(): Stack = {
      @tailrec
      def fallBrick(stack: Stack, brick: Brick): Stack = {
        val (newStack, changed) = stack.moveBrick(brick)
        if (changed) fallBrick(newStack, brick.moveDown())
        else stack
      }
      bricks.toSeq.sortBy(_.a.z).foldLeft(Stack(Set.empty))(fallBrick)
    }

    def wouldFall: Boolean = bricks.exists(moveBrick(_)._2)
  }

  def parseInput(input: List[String]): Stack = {
    val pattern = """(.+),(.+),(.+)~(.+),(.+),(.+)""".r
    val bricks = input.zipWithIndex.map{
      case (pattern(x1,y1,z1,x2,y2,z2), i) =>
        Brick(Point(x1.toInt,y1.toInt,z1.toInt),Point(x2.toInt,y2.toInt,z2.toInt), (i+65).toChar)
    }
    Stack(bricks.toSet)
  }

  def part1(input: List[String]): Long = {
    val stack = parseInput(input).fall()

    stack.bricks.count{ b =>
      !Stack(stack.bricks - b).wouldFall
    }
  }
}