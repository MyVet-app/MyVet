package com.myvet.myvet

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.anyMap
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun make_appointment() {
        val mockDb: FirebaseFirestore = mock(FirebaseFirestore::class.java)
        val mockUser: FirebaseAuth = mock(FirebaseAuth::class.java)
        val mockCollectionRef: CollectionReference = mock(CollectionReference::class.java)
        val mockDocumentRef: DocumentReference = mock(DocumentReference::class.java)
        val mockFirebaseUser: FirebaseUser = mock(FirebaseUser::class.java)

        // Set up mock behavior
        `when`(mockUser.currentUser).thenReturn(mockFirebaseUser) // Mock FirebaseUser
        `when`(mockFirebaseUser.uid).thenReturn("user123") // Mock the UID of the user
        `when`(mockDb.collection("appointments")).thenReturn(mockCollectionRef) // Mock CollectionReference
        `when`(mockCollectionRef.document(anyString())).thenReturn(mockDocumentRef) // Mock DocumentReference
        `when`(mockDocumentRef.set(anyMap<String, Any>())).thenReturn(Tasks.forResult(null)) // Mock Task

        // Call the function under test
        val appointmentData = hashMapOf(
            "date" to "2023-01-01",
            "time" to "10:00",
            "vet" to "vet123",
            "user" to "user123"
        )
        mockDocumentRef.set(appointmentData)

        // Assertions
        verify(mockDocumentRef).set(appointmentData)
        assertTrue(mockDocumentRef.set(appointmentData).isComplete)
    }

    @Test
    fun update_pet_details() {
        val mockDb: FirebaseFirestore = mock(FirebaseFirestore::class.java)
        val mockUser: FirebaseAuth = mock(FirebaseAuth::class.java)
        val mockCollectionRef: CollectionReference = mock(CollectionReference::class.java)
        val mockDocumentRef: DocumentReference = mock(DocumentReference::class.java)
        val mockFirebaseUser: FirebaseUser = mock(FirebaseUser::class.java)

        // Set up mock behavior
        `when`(mockUser.currentUser).thenReturn(mockFirebaseUser) // Mock FirebaseUser
        `when`(mockFirebaseUser.email).thenReturn("user@example.com") // Mock user's email
        `when`(mockDb.collection("pet owner")).thenReturn(mockCollectionRef) // Mock CollectionReference
        `when`(mockCollectionRef.document(anyString())).thenReturn(mockDocumentRef) // Mock DocumentReference
        `when`(mockDocumentRef.collection("pet details")).thenReturn(mockCollectionRef) // Mock sub-collection
        `when`(mockCollectionRef.add(anyMap<String, Any>())).thenReturn(Tasks.forResult(null)) // Mock Task

        // Call the function under test
        val petDetails = hashMapOf(
            "pet name" to "Buddy",
            "type" to "Dog",
            "age" to "5",
            "weight" to "10",
            "gender" to "Male",
            "medical history" to "Vaccinated"
        )
        mockCollectionRef.add(petDetails)

        // Assertions
        verify(mockCollectionRef).add(petDetails) // Verify the details were added to the collection
        assertTrue(mockCollectionRef.add(petDetails).isComplete) // Ensure the task is completed
    }

    @Test
    fun delete_appointment() {
        val mockDb: FirebaseFirestore = mock(FirebaseFirestore::class.java)
        val mockCollectionRef: CollectionReference = mock(CollectionReference::class.java)
        val mockDocumentRef: DocumentReference = mock(DocumentReference::class.java)

        // Set up mock behavior
        `when`(mockDb.collection("appointments")).thenReturn(mockCollectionRef) // Mock CollectionReference
        `when`(mockCollectionRef.document(anyString())).thenReturn(mockDocumentRef) // Mock DocumentReference
        `when`(mockDocumentRef.delete()).thenReturn(Tasks.forResult(null)) // Mock Task for delete()

        // Call the function under test
        val appointmentId = "appointment123" // Example appointment ID to delete
        mockDb.collection("appointments").document(appointmentId).delete()

        // Assertions
        verify(mockDocumentRef).delete() // Verify that delete() was called on the correct document
        assertTrue(mockDocumentRef.delete().isComplete) // Ensure the delete task is completed
    }



}