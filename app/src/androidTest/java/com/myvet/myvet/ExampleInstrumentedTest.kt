import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.idling.CountingIdlingResource
import com.myvet.myvet.MakeAppointment
import com.myvet.myvet.PetOwnerWindow
import com.myvet.myvet.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    var activityRule = ActivityTestRule(PetOwnerWindow::class.java)

    private val idlingResource = CountingIdlingResource("UI_LOADING")

    @Before
    fun setUp() {
        // רישום IdlingResource
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun tearDown() {
        // הסרת IdlingResource לאחר הבדיקה
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun testSuccessfulAppointmentBooking() {
        // סימון התחלת טעינת UI
        idlingResource.increment()

        try {
            // מחכה ש-Activity ייטען
            onView(withId(R.id.main)).check(matches(isDisplayed()))

            // מוודא שהכפתור קיים ולוחץ עליו
            onView(withId(R.id.FindVet))
                .check(matches(isDisplayed()))
                .perform(click())

            // פתיחת מסך קביעת התור
            val appointmentScenario = ActivityTestRule(MakeAppointment::class.java)

            // מחכה שהמסך של קביעת התור יופיע
            onView(withId(R.id.calendarView)).check(matches(isDisplayed()))

            // בחירת וטרינר
            onView(withText("Dr. John Doe"))
                .check(matches(isDisplayed()))
                .perform(click())

            // לחיצה על לוח שנה
            onView(withId(R.id.calendarView))
                .check(matches(isDisplayed()))
                .perform(click())

            // לחיצה על OK
            onView(withText("OK"))
                .check(matches(isDisplayed()))
                .perform(click())

            // מוודא שהודעת האישור מופיעה
            onView(withText("Appointment successfully booked!"))
                .check(matches(isDisplayed()))

        } finally {
            // הבטחה שנסמן שהטעינה הסתיימה גם אם יש שגיאה
            idlingResource.decrement()
        }
    }

}
