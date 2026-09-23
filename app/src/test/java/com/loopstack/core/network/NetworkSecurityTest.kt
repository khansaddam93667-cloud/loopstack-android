package com.loopstack.core.network

import android.security.NetworkSecurityPolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34]) // Or whichever SDK version works with Robolectric
class NetworkSecurityTest {

    @Test
    fun `cleartext is permitted for local domains`() {
        val policy = NetworkSecurityPolicy.getInstance()
        assertTrue("127.0.0.1 should permit cleartext", policy.isCleartextTrafficPermitted("127.0.0.1"))
        assertTrue("localhost should permit cleartext", policy.isCleartextTrafficPermitted("localhost"))
        assertTrue("::1 should permit cleartext", policy.isCleartextTrafficPermitted("::1"))
    }

    @Test
    fun `cleartext is not permitted for external domains`() {
        val policy = NetworkSecurityPolicy.getInstance()
        assertFalse("example.com should not permit cleartext", policy.isCleartextTrafficPermitted("example.com"))
    }
}
