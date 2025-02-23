package com.myvet.myvet

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService

class FirebaseCloudMessagingService : FirebaseMessagingService() {
    companion object {
        const val TAG = "MyFCMService"  // Ensure your TAG is defined
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        sendTokenToFirestore(token) // Call the new function
    }

    private fun sendTokenToFirestore(token: String) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser // Get the current user

        if (user != null) {
            val userId = user.uid // Or however you get the user ID
            val db = FirebaseFirestore.getInstance() // Get reference to Firestore

            val userRef = db.collection("users").document(userId)  // Reference to the user's document
            userRef.update("fcm_tokens", com.google.firebase.firestore.FieldValue.arrayUnion(token)) // Add the token to the array
             .addOnSuccessListener {  // Optional: Add success/failure listeners
                 Log.d(TAG, "Token successfully added to Firestore")
             }
             .addOnFailureListener { e ->
                 Log.w(TAG, "Error adding token to Firestore", e)
             }
        } else {
            // User is not signed in.  Handle this case (e.g., don't try to store the token)
            Log.w(TAG, "User not signed in; cannot save FCM token.")
        }
    }
//
//    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
//        Log.d(TAG, "From: ${message.from}")
//
//        // Check if message contains a data payload.
//        if (message.data.isNotEmpty()) {
//            Log.d(TAG, "Message data payload: ${message.data}")
//            handleDataMessage(message.data)
//        }
//        message.notification?.let { notification ->
//            Log.d(TAG, "Message Notification Body: ${notification.body}")
//            // Process notification data
//            sendNotification(notification.title, notification.body)
//        }
//    }
//
//    private fun handleDataMessage(data: Map<String, String>) {
//        // Handle data payload
//        // Example:
//        val title = data["title"] ?: "FCM Data Message"
//        val message = data["message"] ?: "No message content"
//        Log.d(TAG, "handleDataMessage - title: $title, message: $message")
//        sendNotification(title, message)
//    }
//
//    private fun sendNotification(title: String?, messageBody: String?) {  // Use nullable Strings
//        val intent = Intent(this, MainActivity::class.java) // Replace with the main activity
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
//            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
//
//        val channelId = "fcm_default_channel"  // Replace
//
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(android.R.drawable.ic_dialog_info)  // Replace
//            .setContentTitle(title ?: "FCM Notification")  // Use provided title or a default one
//            .setContentText(messageBody)
//            .setAutoCancel(true)
//            .setSound(defaultSoundUri)
//            .setContentIntent(pendingIntent)
//            .setColor(ContextCompat.getColor(this, android.R.color.holo_blue_bright))  // Replace
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(channelId,
//                "Default channel",  // User-visible name
//                NotificationManager.IMPORTANCE_DEFAULT)
//            notificationManager.createNotificationChannel(channel)
//        }
//        notificationManager.notify(0, notificationBuilder.build())
//    }
}