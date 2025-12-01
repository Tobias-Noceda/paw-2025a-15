<script module lang="ts">
  import { defineMeta } from '@storybook/addon-svelte-csf';
  import Card from './Card.svelte';

  // More on how to set up stories at: https://storybook.js.org/docs/writing-stories
  const { Story } = defineMeta({
    title: 'lib/Card',
    component: Card,
    tags: ['autodocs'],
    argTypes: {
      avatarSrc: { control: 'text' },
      userName: { control: 'text' },
      insurances: { control: 'object' },
      schedule: { control: 'object' },
      email: { control: 'text' },
      phone: { control: 'text' }
    },
    args: {
      avatarSrc: 'http://pawserver.it.itba.edu.ar/paw-2025a-15/supersecret/user-profile-pic/75',
    }
  });
</script>

<!-- More on writing stories with args: https://storybook.js.org/docs/writing-stories/args -->
<Story
  name="Doctor"
  args={{
    variant: 'doctor',
    userName: 'Dr. Test',
    specialization: 'Cardiology',
    insurances: ['Osde', 'Swiss Medical'],
    schedule: { monday: false, tuesday: true, wednesday: true, thursday: true, friday: false, saturday: false, sunday: false }
  }}
/>

<Story
  name="Patient"
  args={{
    variant: 'patient',
    userName: 'Jane Doe',
    email: 'jane.doe@example.com',
    phone: '11 1234-5678'
  }}
/>