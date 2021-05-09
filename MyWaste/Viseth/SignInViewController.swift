//
//  SignInViewController.swift
//  FinalProject
//
//  Created by Prum Viseth on 3/20/20.
//  Copyright © 2020 Apple. All rights reserved.
//

import UIKit
import Firebase
import FirebaseDatabase
import FirebaseAuth

class SignInViewController: UIViewController {
    @IBOutlet weak var textfieldEmail: UITextField!
    @IBOutlet weak var textfieldPassword: UITextField!
    
    @IBOutlet weak var signinBtn: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //Hide keyboard when tapped outside textField
        //This function was implement using extension UIViewController So that Every View Controller Can use
        self.hideKeyboardWhenTappedAround()
        
        //Initially Setting
        //Change cornerRadius of SignIn Button
        signinBtn.layer.cornerRadius = 7
        
        //Changing the cornerRadius of Email TextField to Make it round
        textfieldEmail.layer.cornerRadius = textfieldEmail.frame.height/2
        textfieldEmail.clipsToBounds = true
        
        //Changing the cornerRadius of Password TextField to Make it round
        textfieldPassword.layer.cornerRadius = textfieldPassword.frame.height/2
        textfieldPassword.clipsToBounds = true
    }
    
    override func viewDidAppear(_ animated: Bool) {
        if Auth.auth().currentUser != nil{
            self.navigationController?.popToRootViewController(animated: true)
        }
    }
    
    
    /**********************************************/
    //Sign In Button Acction
    /**********************************************/
    @IBAction func buttSignIn(_ sender: Any) {
        
        //Getting Information From Text Field
        //While Trimming Text from any Space
        let email = textfieldEmail.text!.trimmingCharacters(in: .whitespacesAndNewlines)
        let password = textfieldPassword.text!.trimmingCharacters(in: .whitespacesAndNewlines)
    
        //Call Sign In Function From FirebaseAuth
        Auth.auth().signIn(withEmail: email, password: password) {
            (results, error) in
            
            //Check If There Any Error And Print Them
            if error != nil{

                print(error!)
                //Function Added in the UIViewController Extention for Showing Message Dialog Box
                self.showMessageDialog(title: "Sign In Failed", message: "Please Check Your Email or Password Again", buttonCancelMessage: "OK")
            } else {
                
                //If No Error Found then return the ViewController that is Launch From
                //(Depend on the Tab bar Location)
                self.navigationController?.popToRootViewController(animated: true)
                //Function Added in the UIViewController Extention for Showing Message Dialog Box
                self.showMessageDialog(title: "Sign In Successful", message: "Welcome To MyWaste Application", buttonCancelMessage: "Awesome!")
            }//End of Error Checking
            
        }//End Of Auth.auth.signIn Function
        
    }//End of Sign In Button Function
    
}//End Of Class SignInViewController
