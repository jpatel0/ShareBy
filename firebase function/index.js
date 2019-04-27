'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/Groups/{country}/{pin}/{key1}/{key2}/posts/{push_id}')
                                        .onWrite((snapshot, context) => {

        const country = context.params.country;
        const pin = context.params.pin;
        const key1 = context.params.key1;
        const key2 = context.params.key2;
        const push_id = context.params.push_id;
        const data = snapshot.after.val();

        if (data) {
            admin.database().ref(`/Groups/${country}/${pin}/${key1}/${key2}/posts/${push_id}`)
                                                .once("value", (snapshot) => {
                var data = snapshot.val();
                const reqUid = data.reqUid;
                var payload;
                console.log(data);
                if (data.sharedUid == undefined || data.sharedUid == null) {

                    if (data.priority == 0) {
                        payload = {
                                "notification":{
                                    "title" : "A new member has joined",
                                    "body" : `${data.name} has joined your neighborhood`,
                                    "sound" : "default",
                                    "icon" : "ic_home"
                                },
                                "data" : {
                                    "extra" : "some extra"
                                }
                        };
                    }else {
                        payload = {
                                "notification" : {
                                    "title" : "Item Request",
                                    "body" : `${data.name} has requested a/an ${data.title}`,
                                    "sound" : "default",
                                    "icon" : "ic_home"
                                },
                                "data" : {
                                    "extra" : "some extra"
                                }
                        };
                    }

                    admin.database().ref(`/Groups/${country}/${pin}/${key1}/${key2}/members`).once("value", (snapshot) => {
                        var memberTokens = [];

                        snapshot.forEach((childSnapshot) => {
                            if (childSnapshot.key != reqUid) {
                                memberTokens.push(childSnapshot.val());
                            }
                        });
                        if (memberTokens.length>0) {
                            admin.messaging().sendToDevice(memberTokens, payload)
                                                              .then(function(response) {
                                                                console.log('Successfully sent message:', response);
                                                              })
                                                              .catch(function(error) {
                                                                console.log('Error sending message:', error);
                                                              });
                        }
                    });
                }
            });
        }else {
            console.log('Node deleted');
        }
});



// exports.updateTokenInGroup = functions.database.ref('/Groups/{country}/{pin}/{key1}/{key2}/members/{member_id}')
//                                                     .onWrite((snapshot,context) => {
//
//         const member_id = context.params.member_id;
//         var token = snapshot.after.val();
//         if (member) {
//             if (token != undefined && token != 'true') {
//                 return admin.database().ref(`/UserDetails/${}`)
//             }
//         }else {
//             console.log('Member left',snapshot.before.val());
//         }
// });
