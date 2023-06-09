package com.ingenifi.priorifi

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TaskServiceTest(@Autowired val service: TaskService) {

    @Test
    fun `should fail when name is not there`() {
        val task = service.createTask(Task(null, "", "description")).block()
        task?.should {
            it.isLeft() shouldBe true
            it.leftOrNull()?.get(0)?.errorMessage shouldBe "Task name is missing"
        }
    }

    @Test
    fun `should succeed when name is there`() {
        val task = service.createTask(Task(null, "name", "description")).block()
        task?.should {
            it.isRight() shouldBe true
            it.getOrNull() shouldBe Task("1", "name", "description")
        }
    }

}

