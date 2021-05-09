//
//  SignUpViewController.swift
//  FinalProject
//
//  Created by Prum Viseth on 3/20/20.
//  Copyright © 2020 Apple. All rights reserved.
//

import UIKit
import Firebase
import FirebaseStorage

class SignUpViewController: UIViewController{
    
    //Element
    @IBOutlet weak var profilePicture: UIImageView!
    @IBOutlet weak var fullnametextField: UITextField!
    @IBOutlet weak var emailtextField: UITextField!
    @IBOutlet weak var passwordtextField: UITextField!
    @IBOutlet weak var phonenumbertextField: UITextField!
    @IBOutlet weak var buttSignup: UIButton!
    @IBOutlet weak var buttSignIn: UIButton!
    
    //Initializing UiimagePickerController
    var imagePicker = UIImagePickerController()
    
    //Initialize Variable image for Further Usage
    var image: UIImage? = nil
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //Functio to hide keyboard when tap around
        self.hideKeyboardWhenTappedAround()
        
        //Connect ImagePickerViewController to the SignUpViewController
        imagePicker.delegate = self
        
        //Hide Back Button on the Navigation Bar
        self.navigationItem.setHidesBackButton(true, animated: true)
        
        
        
        //Initially Change the cornerRadius to make look good
        buttSignup.layer.cornerRadius = 10
        
        //Programmatically change the keyboardType of PhoneNumberTextField to PhonePad
        phonenumbertextField.keyboardType = .phonePad
        
    }//End of ViewDidLoad()
    
    
    override func viewDidAppear(_ animated: Bool) {
        
        //Check If the application have any user signin
        //If user just Sign in or is Sign In Move to the ViewController that is Lauch From
        if Auth.auth().currentUser != nil {
            self.navigationController?.popToRootViewController(animated: true)
        }
    }//End of ViewDidAppear
    
    
    
    //To Clean All Fields For User Input
    func validateField() -> Bool {
        if fullnametextField.text?.trimmingCharacters(in: .whitespacesAndNewlines) == "" ||
            emailtextField.text?.trimmingCharacters(in:.whitespacesAndNewlines) == "" ||
            passwordtextField.text?.trimmingCharacters(in: .whitespacesAndNewlines) == "" ||
            phonenumbertextField.text?.trimmingCharacters(in: .whitespacesAndNewlines) == "" {

            return false
        }
        return true
    }//End of ValidateField()
    
    
    
    //Tapped To Change ProfilePicture
    @IBAction func changeProfile(_ sender: Any) {
        
        profilePicture.clipsToBounds = true
        imagePicker.sourceType = .photoLibrary
        imagePicker.allowsEditing = true
        present(imagePicker, animated: true, completion: nil)
    }
    
    //Tapped To SignUp
    @IBAction func signUp(_ sender: Any) {
        
        //Gatter Text From TextField For Further User While Checking for Spaces
        let fullname = fullnametextField.text!.trimmingCharacters(in: .whitespacesAndNewlines)
        let email = emailtextField.text!.trimmingCharacters(in:.whitespacesAndNewlines)
        let password = passwordtextField.text!.trimmingCharacters(in: .whitespacesAndNewlines)
        let phonenumber = phonenumbertextField.text!.trimmingCharacters(in: .whitespacesAndNewlines)
        
        //Check if TextFields are Empty
        if validateField(){
            
            //Check for Default Image
            //If yes, Perform Saving Data without Profile Picture
            if profilePicture.image != UIImage.init(systemName: "person.crop.circle") {
                
                //Call CreateUser Function From FirebaseAuth to Create User
                //Create Account Without Profile Picture
                Auth.auth().createUser(withEmail: email, password: password) {
                    (result , error) in
                    
                    //Check For Error when creating User
                    if error != nil {
                        
                        print(error as? String ?? "Something Went Wrong And We Don't Know")
                        self.showMessageDialog(title: "Sign Up Faild", message: "\(String(describing: error))", buttonCancelMessage: "Got It")
                    }else {
                        
                        //Referencing the database
                        let ref = Database.database().reference()
                        
                        //Add User to the database
                        ref.child("Account/\(Auth.auth().currentUser?.uid ?? "\(ref.childByAutoId())")").setValue([
                            "fullname": fullname,
                             "email": email,
                             "password": password,
                             "phonenumber": phonenumber,
                        ])
                        
                        
                        //After Account Created Move Back to the previous ViewController
                        self.navigationController?.popToRootViewController(animated: true)
                        self.showMessageDialog(title: "Sign Up Successful", message: "You haven't add any profile picture\nYou add it later one", buttonCancelMessage: "Awesome")
                        
                        
                    }//End of Error Checkong
                }//End of Auth
                
            }//End of Sign up without Profile Image
            else{
                //Compress Image Before Sending to the Firebase Storage
                guard let imageData = profilePicture.image?.jpegData(compressionQuality: 0.4) else{
                    return
                }
            
                //Generate Random String for Image Name
                let randomID = UUID.init().uuidString
                //Referencing the Path to the Firebase Storage for Saving and Loading
                let storageRef = Storage.storage().reference()
                let storageprofileRef = storageRef.child("profile/\(randomID)")
                
                
                let metadata = StorageMetadata()
                metadata.contentType = "image/jpg"
                //Storing Image into The Firebase Storage
                storageprofileRef.putData(imageData, metadata: metadata, completion: {
                    (storageMetaData, error) in
                    
                    //Check For Error
                    if error != nil{
                        print (error?.localizedDescription as Any)
                        return
                    }else{
                        storageprofileRef.downloadURL(completion: {
                            (URL, error) in
                            //Create User (Account and Password) with Profile Picture
                            Auth.auth().createUser(withEmail: email, password: password) {
                                (result , error) in

                                //Check For Error when creating User
                                if error != nil {

                                    print(error.debugDescription)
                                    self.showMessageDialog(title: "Sign Up Faild", message: "\(error.debugDescription)", buttonCancelMessage: "Got It")
                                }else {
                                    print("Successful")

                                    //Referencing Firebase Database for Storage
                                    let ref = Database.database().reference()

                                    //Add User
                                    ref.child("Account/\(Auth.auth().currentUser?.uid ?? "\(ref.childByAutoId())")").setValue([
                                        "fullname": fullname,
                                         "email": email,
                                         "password": password,
                                         "phonenumber": phonenumber,
                                         "profilepic" : "\(URL!)"
                                    ])

                                    self.navigationController?.popToRootViewController(animated: true)
                                    self.showMessageDialog(title: "Sign Up Successful", message: "", buttonCancelMessage: "Awesome!")
                                }
                        
                            }//End of Auth
                        })//End of DownloadURL Function
                    }//End of Error Checking
                })//End of PutData Function
            }//End of Sign Up with Profile Picture
        }else{
            //Show Message Dialog Box When Sign Up Failed
            self.showMessageDialog(title: "Sign Up Failed", message: "Key information is missing", buttonCancelMessage: "OK")
            
        }//End of ValidateString()
        
    }
    
    
    
    
} //End Of Class SignUpViewController

//Adding ImagePickerController into SignupViewController
extension SignUpViewController: UIImagePickerControllerDelegate, UINavigationControllerDelegate{
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        if let imageSelected = info[UIImagePickerController.InfoKey.originalImage] as? UIImage {
            image = imageSelected
            profilePicture.image = imageSelected
        }
        dismiss(animated: true, completion: nil)
    }
    
}//End of SignUpViewController Extension

//Add function to Any Class that Inherite UIViewController
//So that any ViewController can use easily

extension UIViewController{
    
    //Function to dismiss Keyboard when tapped outside TextField
    func hideKeyboardWhenTappedAround() {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIViewController.dismissKeyboard))
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
    }

    @objc func dismissKeyboard() {
        view.endEditing(true)
    }
}//End of UIViewController
