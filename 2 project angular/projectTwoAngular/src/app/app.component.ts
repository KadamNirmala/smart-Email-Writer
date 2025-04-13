import { Component, ElementRef, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NzMessageService } from 'ng-zorro-antd/message';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],

})
export class AppComponent {

  emailContent: string = '';
  selectedTone: string = 'Formal';
  tones: string[] = ['Formal', 'Casual', 'Professional', 'Friendly'];
  generatedEmail: string = '';

  constructor(private http: HttpClient, private message: NzMessageService) { }

  generateEmail() {
    if (!this.emailContent.trim()) {
      this.message.warning('Please enter email content.');
      return;
    }


    const requestData = {
      emailContent: this.emailContent.trim().replace(/\n/g, ' '), // Removes new lines
      tone: this.selectedTone.toLowerCase() // Ensure it's lowercase like "formal"
    };

    this.http.post('http://localhost:8080/api/email/generate', requestData, { responseType: 'text' }) // Ensure response is treated as text
      .subscribe({
        next: (response) => {
          console.log("Email Generated Successfully:", response);
          this.generatedEmail = response; // Store response if needed
          this.message.success('Email Generated Successfully.');
        },
        error: (error) => {
          console.error("Error Generating Email:", error);
          this.message.error("Failed to generate email."); // Show error message
        }
      });

  }

  copyToClipboard() {
    navigator.clipboard.writeText(this.generatedEmail).then(() => {
      this.message.success('Copied to clipboard!');
    }).catch(() => {
      this.message.error('Failed to copy.');
    });
  }

}
