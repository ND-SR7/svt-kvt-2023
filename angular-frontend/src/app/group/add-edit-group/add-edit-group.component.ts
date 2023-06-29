import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/user/services/authentication.service';
import { Group } from "src/app/group/model/group.model";

@Component({
  selector: 'app-add-edit-group',
  templateUrl: './add-edit-group.component.html',
  styleUrls: ['./add-edit-group.component.css']
})
export class AddEditGroupComponent implements OnInit {

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authenticationService: AuthenticationService,
    private router: Router
  ) { 
    this.form = this.fb.group({
      name: [null, Validators.required],
      description: [null, Validators.required],
      creationDate: [null, Validators.required],
      isSuspended: [null, Validators.required],
      suspendedReason: [null]
    });
   }

   ngOnInit(): void {
     
   }

   submit() {
    const group: Group = new Group;
		group.name = this.form.value.name;
		group.description = this.form.value.description;
    group.creationDate = this.form.value.creationDate;
    group.isSuspended = this.form.value.isSuspended;
    group.suspendedReason = this.form.value.suspendedReason;
// todo
		// this.authenticationService.login(auth).subscribe(
		// 	result => {
		// 		window.alert('Successful login!');
		// 		localStorage.setItem('user', JSON.stringify(result));
		// 		this.router.navigate(['groups']);
		// 	},
		// 	error => {
    //     window.alert('An error occurred!');
		// 		console.log(error);
		// 	}
		// );
  }
}
