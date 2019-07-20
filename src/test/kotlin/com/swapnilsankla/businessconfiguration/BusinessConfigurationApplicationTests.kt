package com.swapnilsankla.businessconfiguration

import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class BusinessConfigurationApplicationTests {

    companion object {
        private lateinit var mongodExecutable: MongodExecutable

        @BeforeClass
        @JvmStatic
        fun setUp() {
            val mongodConfig = MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION)
                    .net(Net("localhost", 27017, Network.localhostIsIPv6()))
                    .build()

            val starter = MongodStarter.getDefaultInstance()
            mongodExecutable = starter.prepare(mongodConfig)
            mongodExecutable.start()
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            mongodExecutable.stop()
        }
    }


    @Test
    fun contextLoads() {
    }
}
