import { Component } from '@angular/core';
import { Group } from '../model/group.model';
import { User } from 'src/app/user/model/user.model';
import { GroupRequest } from '../model/groupRequest.model';
import { GroupService } from '../services/group.service';
import { UserService } from 'src/app/user/services/user.service';
import { ToastrService } from 'ngx-toastr';
import { JwtHelperService } from '@auth0/angular-jwt';
import { GroupIndex } from '../model/groupIndex.model';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Hit } from '../model/hit.model';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-search-group',
  templateUrl: './search-group.component.html',
  styleUrls: ['./search-group.component.css']
})
export class SearchGroupComponent {
  groupIndexes: GroupIndex[] = [];
  highlights: Map<GroupIndex, Map<string, string[]>> = new Map();

  user: User = new User();
  isAdmin: boolean = false;

  groupRequests: Map<number, GroupRequest[]> = new Map();
  userGroups: Group[] = [];

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private groupService: GroupService,
    private userService: UserService,
    private toastr: ToastrService,
    private sanitizer: DomSanitizer
  ) {
    this.form = this.fb.group({
      name: [''],
      description: [''],
      fileContent: [''],
      minPosts: [null],
      maxPosts: [null],
      minLikes: [null],
      maxLikes: [null],
      operator: ['OR']
    });
  }

  ngOnInit(): void {
    this.groupService.getAll().subscribe(
      result => {
        const temp = result.body as Group[];

        temp.forEach(group => {
          this.groupService.getGroupRequests(group.id).subscribe(
            result => {
              this.groupRequests.set(group.id, result.body as GroupRequest[]);
            }
          );
        });
      }
    );

    let sub, role: string;
    const item = localStorage.getItem('user') || '';

    const jwt: JwtHelperService = new JwtHelperService();
		const decodedToken = jwt.decodeToken(item);
    sub = decodedToken.sub;
    role = decodedToken.role.authority;

    if (role.includes('ADMIN'))
      this.isAdmin = true;

    this.userService.getOneByUsername(sub).subscribe(
      result => {
        this.user = result.body as User;

        this.userService.getUserGroups(this.user.id).subscribe(
          result => {
            this.userGroups = result.body as Group[];
          }
        );
      }
    );
  }

  canAccess(groupId: number): boolean {
    let access: boolean = false;
    
    this.userGroups.forEach(group => {
      if (group.id == groupId)
        access = true;
    });

    return access;
  }

  canSendRequest(groupId: number): boolean {
    let canSend: boolean = true;
    this.groupRequests.forEach(requests => {
      requests.forEach(request => {
        if (request.createdByUserId == this.user.id && request.forGroupId == groupId)
          canSend = false;
      });
    });

    if (this.canAccess(groupId))
      canSend = false;

    return canSend;
  }

  sendRequest(groupId: number): void {
    const groupRequest: GroupRequest = new GroupRequest();
    groupRequest.createdAt = new Date().toISOString().slice(0, -1);
    groupRequest.createdByUserId = this.user.id;
    groupRequest.forGroupId = groupId;

    this.groupService.sendGroupRequest(groupRequest).subscribe(
      result => {
        this.toastr.success('Successfully sent request to join group');
        location.reload();
      },
      error => {
        this.toastr.error('Error while sending group request');
        console.log(error);
      }
    );
  }

  // pretraga grupa
  submit(): void {
    if (this.form.valid) {
      const params: Map<string, string> = new Map();
      const name = this.form.value.name;
      const description = this.form.value.description;
      const fileContent = this.form.value.fileContent;
      const minPosts = this.form.value.minPosts;
      const maxPosts = this.form.value.maxPosts;
      const minLikes = this.form.value.minLikes;
      const maxLikes = this.form.value.maxLikes;
      const operator = this.form.value.operator;

      if (name !== '')        params.set('name', name);
      if (description !== '') params.set('description', description);
      if (fileContent !== '') params.set('fileContent', fileContent);
      if (minPosts !== null)    params.set('minPosts', minPosts);
      if (maxPosts !== null)    params.set('maxPosts', maxPosts);
      if (minLikes !== null)    params.set('minLikes', minLikes);
      if (maxLikes !== null)    params.set('maxLikes', maxLikes);

      params.set('operator', operator);
      
      this.groupIndexes = [];
      this.highlights = new Map();

      this.groupService.searchGroups(params).subscribe(
        (result) => {
          const element = document.getElementById('groupSearchHeading')!;
          
          if (result.length > 0) {
            element.innerText = 'Found groups:';
          } else {
            element.innerText = 'No groups found';
          }

          result.forEach((hit: Hit) => {
            this.groupIndexes.push(hit.source);
            this.highlights.set(hit.source, hit.highlights)
          });
        },
        (error) => {
          console.error('Error while searching:', error);
        }
      );
    }
  }

  // pomocna funkcija za dobavlanje highlights-a za index
  getHighlightsForIndex(index: GroupIndex): Map<string, string[]> {
    return this.highlights.get(index)!;
  }

  // pomocna funkcija za renderovanje highlights-a kao HTML
  parseAsHTML(htmlString: string) {
    htmlString = htmlString.replaceAll('<em>', '<b style="color: blue;">');
    htmlString = htmlString.replaceAll('</em>', '</b>');
    return this.sanitizer.bypassSecurityTrustHtml(htmlString);
  }
}
