package com.ingenifi.priorifi

import io.kotest.assertions.asClue
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TaskServiceTest(@Autowired val service: TaskService) {

    @Test
    fun `should fail when name is not there`() {
        val task = service.createTask(Task(null, "", "description"))
        task.should {
            it.isLeft() shouldBe true
            it.leftOrNull()?.get(0)?.errorMessage shouldBe "Task name is missing"
        }
    }

    @Test
    fun `should succeed when name is there`() {
        val request = Task(null, "name", "description")
        service.createTask(request).should {
            it.isRight() shouldBe true
            it.getOrNull()?.asClue { task ->
                task.name shouldBe request.name
                task.description shouldBe request.description
            }
        }
    }

}

