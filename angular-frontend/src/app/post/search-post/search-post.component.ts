import { Component } from '@angular/core';
import { PostIndex } from '../model/postIndex.model';
import { FormBuilder, FormGroup } from '@angular/forms';
import { PostService } from '../services/post.service';
import { ToastrService } from 'ngx-toastr';
import { DomSanitizer } from '@angular/platform-browser';
import { Hit } from '../model/hit.model';

@Component({
  selector: 'app-search-post',
  templateUrl: './search-post.component.html',
  styleUrls: ['./search-post.component.css']
})
export class SearchPostComponent {
  postIndexes: PostIndex[] = [];
  highlights: Map<PostIndex, Map<string, string[]>> = new Map();

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private postService: PostService,
    private toastr: ToastrService,
    private sanitizer: DomSanitizer
  ) {
    this.form = this.fb.group({
      title: [''],
      fullContent: [''],
      fileContent: [''],
      minLikes: [null],
      maxLikes: [null],
      minComments: [null],
      maxComments: [null],
      commentContent: [''],
      operator: ['OR']
    });
  }

  // pretraga objava
  submit(): void {
    if (this.form.valid) {
      const params: Map<string, string> = new Map();
      const title = this.form.value.title;
      const fullContent = this.form.value.fullContent;
      const fileContent = this.form.value.fileContent;
      const minLikes = this.form.value.minLikes;
      const maxLikes = this.form.value.maxLikes;
      const minComments = this.form.value.minComments;
      const maxComments = this.form.value.maxComments;
      const commentContent = this.form.value.commentContent;
      const operator = this.form.value.operator;

      if (title !== '')          params.set('title', title);
      if (fullContent !== '')    params.set('fullContent', fullContent);
      if (fileContent !== '')    params.set('fileContent', fileContent);
      if (minLikes !== null)     params.set('minLikes', minLikes);
      if (maxLikes !== null)     params.set('maxLikes', maxLikes);
      if (minComments !== null)  params.set('minComments', minComments);
      if (maxComments !== null)  params.set('maxComments', maxComments);
      if (commentContent !== '') params.set('commentContent', commentContent);

      params.set('operator', operator);
      
      this.postIndexes = [];
      this.highlights = new Map();

      this.postService.searchPosts(params).subscribe(
        (result) => {
          const element = document.getElementById('postSearchHeading')!;
          
          if (result.length > 0) {
            element.innerText = 'Found posts:';
          } else {
            element.innerText = 'No posts found';
          }

          result.forEach((hit: Hit) => {
            this.postIndexes.push(hit.source);
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
  getHighlightsForIndex(index: PostIndex): Map<string, string[]> {
    return this.highlights.get(index)!;
  }

  // pomocna funkcija za renderovanje highlights-a kao HTML
  parseAsHTML(htmlString: string) {
    htmlString = htmlString.replaceAll('<em>', '<b style="color: blue;">');
    htmlString = htmlString.replaceAll('</em>', '</b>');
    return this.sanitizer.bypassSecurityTrustHtml(htmlString);
  }
}
