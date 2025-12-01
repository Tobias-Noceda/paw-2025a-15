<script module>
  import { defineMeta } from '@storybook/addon-svelte-csf';
  import Avatar from './Avatar.svelte';

  // More on how to set up stories at: https://storybook.js.org/docs/writing-stories
  const { Story } = defineMeta({
    title: 'lib/Avatar',
    component: Avatar,
    tags: ['autodocs'],
    argTypes: {
      src: { control: 'text' },
      size: { control: { type: 'select', options: ['sm', 'md', 'lg'] } },
      class: { control: 'text' }
    }
  });
</script>

<!-- More on writing stories with args: https://storybook.js.org/docs/writing-stories/args -->
<Story name="Small" args={{ size: 'sm' }} />

<Story name="Medium" args={{ size: 'md' }} />

<Story name="Large" args={{ size: 'lg' }} />

<Story name="Extra Large" args={{ size: 'xl' }} />

<Story name="Border" args={{ src: 'http://pawserver.it.itba.edu.ar/paw-2025a-15/supersecret/user-profile-pic/7', size: 'xl', class: 'bg-primary' }} />

<Story name="Auto Size" args={{ src: 'http://pawserver.it.itba.edu.ar/paw-2025a-15/supersecret/user-profile-pic/7', size: 'auto', class: 'bg-primary !max-w-[30%] !max-h-[30%]', }} />