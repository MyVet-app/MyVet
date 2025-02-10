import android.Manifest
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.ViewMatchers.hasSibling
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.myvet.myvet.PetOwnerWindow
import com.myvet.myvet.R
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(PetOwnerWindow::class.java)

    @get:Rule
    val fineLocationPermissionRule: GrantPermissionRule = GrantPermissionRule
        .grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @get:Rule
    val coarseLocationPermissionRule: GrantPermissionRule = GrantPermissionRule
        .grant(Manifest.permission.ACCESS_COARSE_LOCATION)

    private lateinit var vetLoadingIdlingResource: CountingIdlingResource

    @Before
    fun setUp() {
        // רישום IdlingResource
//        IdlingRegistry.getInstance().register(vetLoadingIdlingResource)

        // Wait for the MakeVet activity to be launched
//        val makeVetActivityScenario = ActivityScenario.launch(FindVet::class.java)
//
//        // Register the IdlingResource from MakeVet activity
//        makeVetActivityScenario.onActivity { activity ->
//            vetLoadingIdlingResource = activity.getIdlingResource()
//            IdlingRegistry.getInstance().register(vetLoadingIdlingResource)
//        }

        val authTask = FirebaseAuth.getInstance()
            .signInWithEmailAndPassword("petowner@gmail.com", "Password1!")

        Tasks.await(authTask) // Blocks until sign-in completes
        if (authTask.isSuccessful) {
            ActivityScenario.launch(PetOwnerWindow::class.java)
        }
    }

    @After
    fun tearDown() {
        // הסרת IdlingResource לאחר הבדיקה
//        IdlingRegistry.getInstance().unregister(vetLoadingIdlingResource)

        ActivityScenario.launch(PetOwnerWindow::class.java)
        Thread.sleep(5000)

        onView(
            allOf(
                withText("Delete"), hasSibling(
                    withText(
                        "Dr. Google Vet\n" +
                                "2025-02-10 10:30 - 10:45"
                    )
                )
            )
        )
            .check(matches(isDisplayed()))
            .perform(click())

    }

    @Test
    fun testSuccessfulAppointmentBooking() {
        // מחכה ש-Activity ייטען
//            onView(withId(R.id.main)).check(matches(isDisplayed()))
        onView(withId(R.id.HelloText)).check(matches(isDisplayed()))

        // מוודא שהכפתור קיים ולוחץ עליו
        onView(withId(R.id.FindVet))
            .check(matches(isDisplayed()))
            .perform(click())

//        // Wait for the MakeVet activity to be launched
//        val makeVetActivityScenario = ActivityScenario.launch(FindVet::class.java)
//
//        // Register the IdlingResource from MakeVet activity
//        makeVetActivityScenario.onActivity { activity ->
//            vetLoadingIdlingResource = activity.getIdlingResource()
//            IdlingRegistry.getInstance().register(vetLoadingIdlingResource)
//        }
        Thread.sleep(5000)

        // בחירת וטרינר
        onView(allOf(withText("SELECT"), hasSibling(withSubstring("Google Vet"))))
            .check(matches(isDisplayed()))
            .perform(click())

        // לחיצה על לוח שנה
        onView(withText("Available appointments:"))
            .check(matches(isDisplayed()))

        Thread.sleep(5000)

        // לחיצה על OK
        onView(allOf(withText("SELECT"), hasSibling(withText("10:30 - 10:45"))))
            .check(matches(isDisplayed()))
            .perform(click())

        // מוודא שהודעת האישור מופיעה
        onView(withSubstring("10:30 - 10:45"))
            .check(matches(isDisplayed()))
        }
}