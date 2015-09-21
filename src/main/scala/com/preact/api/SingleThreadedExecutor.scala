package com.preact.api

import java.util.concurrent._

import org.slf4j.LoggerFactory


final class SingleThreadedExecutor
    extends ThreadPoolExecutor(0,1,60L,TimeUnit.SECONDS,new LinkedBlockingQueue[Runnable],new ThreadFactory {
  override def newThread(r: Runnable): Thread = {
    val t = new Thread(r)
    t.setName("PreactCtx-"+t.getName)
    t setDaemon true
    t
  }
}){

  lazy val logger = LoggerFactory.getLogger(getClass)
  private val delayForNextTask: Long = 200
  override def execute(task: Runnable): Unit = {
    logger.debug("|P-OutsideCtxt|")
    super.execute(task)
//    super.execute(new Runnable {
//      override def run(): Unit = {
//        logger.debug("|P-InsideCtx|")
//        task.run()//:-)
//        Thread.sleep(delayForNextTask)
//      }
//    })
  }
}
