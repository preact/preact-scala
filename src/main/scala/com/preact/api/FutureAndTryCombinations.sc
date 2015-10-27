import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Success, Failure, Try}

val futuresWithTry:List[Future[Try[Int]]] = List(
  Future(Try(1)),
  Future(Try(2)),
  Future(Try(3)),
  Future.successful(Failure(new RuntimeException)),
  Future(Try(4))
)

val simpleFutures:List[Future[Int]] = List(
  Future(1),
  Future(2),
  Future(3),
  Future.failed(new RuntimeException),
  Future(4)
)

val simpleTries:List[Try[Int]] = List(
  Success(1),
  Success(2),
  Success(3),
  Failure(new RuntimeException),
  Success(4)
)

//will contain the numbers and the exception
Await.result(Future.sequence(futuresWithTry),Duration.Inf)

//will return the failure when encountered
val futureWithTryReduced2 = for(seq <- Future.sequence(futuresWithTry)) yield seq.foldLeft(Try(0)){case (l, r) => l.flatMap(vl => r.map(vr => vr + vl))}
Await.result(futureWithTryReduced2, Duration.Inf)

//will apply the operation only on valid values
val futureWithTryOperation = Future.sequence(futuresWithTry).map(p => p.map( _.map(x=>x+1)))
Await.result(futureWithTryOperation,Duration.Inf)

//simple tries behaves the same as Future[Try[T]] but, we're loosing the async power
for(entry <- simpleTries) yield entry.map(_+1)
simpleTries.foldLeft(Try(0)){case (l, r) => l.flatMap(vl => r.map(vr => vr + vl))}

// will blow when the failure is encountered
Await.result(Future.sequence(simpleFutures),Duration.Inf)
val futureOperation = Future.sequence(simpleFutures).map(p => p.map(x=>x+1))
//will blow here also
Await.result(futureOperation,Duration.Inf)



