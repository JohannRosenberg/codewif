package com.mydomain.myapp.codewif

import com.codewif.framework.models.TestResult
import com.codewif.framework.models.UnitTest
import com.codewif.framework.testing.TestSetup
import kotlinx.coroutines.delay

class MathUnitTests : TestSetup() {

    init {
        addTest(UnitTest("Add numbers").testToRunSync {
            delay(300)
            val testResult = TestResult()

            // Do your test here. We'll fake the test and just return true.
            testResult.succeeded = true

            // Return some extra information from the test. Usually this contains details from an exception or failed test, but
            // you can also return anything in the details field, even for a successful test.
            testResult.details =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Feugiat pretium nibh ipsum consequat nisl vel pretium lectus quam. Tincidunt arcu non sodales neque sodales ut etiam. Diam maecenas sed enim ut sem viverra aliquet. Sagittis id consectetur purus ut faucibus pulvinar elementum integer enim. In metus vulputate eu scelerisque. Diam quis enim lobortis scelerisque fermentum. Mauris ultrices eros in cursus turpis massa. Semper quis lectus nulla at volutpat diam. Consectetur lorem donec massa sapien faucibus et molestie.\n\n" +
                        "Nunc scelerisque viverra mauris in aliquam sem fringilla ut. Elit at imperdiet dui accumsan sit amet nulla. Adipiscing tristique risus nec feugiat in fermentum posuere urna. Volutpat blandit aliquam etiam erat velit scelerisque in. Tristique senectus et netus et malesuada fames ac turpis egestas. Blandit massa enim nec dui nunc. At imperdiet dui accumsan sit. Vitae tortor condimentum lacinia quis vel eros donec ac. In nibh mauris cursus mattis molestie a. Ullamcorper a lacus vestibulum sed arcu non odio. Odio aenean sed adipiscing diam. Nisl nisi scelerisque eu ultrices vitae auctor eu. Vestibulum sed arcu non odio euismod lacinia at. Pharetra vel turpis nunc eget lorem dolor sed viverra. Ac turpis egestas integer eget aliquet. Nulla facilisi etiam dignissim diam quis enim lobortis scelerisque fermentum. Sollicitudin tempor id eu nisl. Enim tortor at auctor urna nunc id cursus metus aliquam. Ornare massa eget egestas purus. Urna molestie at elementum eu facilisis sed.\n\n" +
                        "Vitae purus faucibus ornare suspendisse. Nec ullamcorper sit amet risus nullam eget. Semper auctor neque vitae tempus. Rutrum quisque non tellus orci. Nisi lacus sed viverra tellus in hac habitasse. Mi tempus imperdiet nulla malesuada. Quis vel eros donec ac odio tempor. Est lorem ipsum dolor sit amet consectetur. Neque gravida in fermentum et sollicitudin ac orci phasellus. Sit amet nisl purus in mollis. Vitae auctor eu augue ut. Quam pellentesque nec nam aliquam sem et. Sed euismod nisi porta lorem mollis aliquam ut porttitor.\n\n" +
                        "Ut sem nulla pharetra diam sit amet. Neque viverra justo nec ultrices dui. Adipiscing tristique risus nec feugiat in fermentum. Auctor elit sed vulputate mi sit amet mauris commodo quis. Urna id volutpat lacus laoreet non curabitur. Erat velit scelerisque in dictum non consectetur a. Mauris pellentesque pulvinar pellentesque habitant. Sagittis id consectetur purus ut faucibus. Viverra vitae congue eu consequat ac felis donec et. Varius sit amet mattis vulputate enim nulla. Platea dictumst quisque sagittis purus. Quam pellentesque nec nam aliquam sem et. Malesuada fames ac turpis egestas integer eget aliquet nibh praesent. Bibendum neque egestas congue quisque. Senectus et netus et malesuada fames ac turpis. Duis ultricies lacus sed turpis tincidunt. Hendrerit gravida rutrum quisque non tellus orci ac auctor. Pellentesque pulvinar pellentesque habitant morbi tristique senectus et."

            testResult
        })

        addTest(UnitTest(testName = "Subtract numbers", skipTest = true).testToRunSync {

            val testResult = TestResult()
            testResult.succeeded = true
            testResult
        })

        addTest(UnitTest("Square root").testToRunSync {
            val testResult = TestResult()
            testResult.succeeded = true
            testResult
        })

        addTest(UnitTest("Long computation A", skipTest = true).testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Long computation B").testToRunAsync { callback ->
            delay(100)
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Compute Prime").testToRunSync {
            // Simulate a long running process
            delay(600)
            val testResult = TestResult()
            testResult.succeeded = true
            testResult
        })

        addTest(UnitTest("Subtract 1").testToRunAsync { callback ->
            delay(100)
            val testResult = TestResult()
            testResult.succeeded = false
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 2").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 3").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = false
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 4").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 5").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 6").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 7").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 8").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 9").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 10").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 11").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 12").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 13").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 14").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 15").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 16").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 17").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 18").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 19").testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = false
            callback.invoke(testResult)
        })

        addTest(UnitTest("Subtract 20", true).testToRunAsync { callback ->
            val testResult = TestResult()
            testResult.succeeded = true
            callback.invoke(testResult)
        })
    }
}